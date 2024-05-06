package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.ContainerHelper
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.entity.player.StackedContents
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.StackedContentsCompatible
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.FurnaceBlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.joml.Math.lerp
import org.joml.Math.min
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaProperties.HEAT
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.ceil
import kotlin.math.max

class EngineBlockEntity(pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(EurekaBlockEntities.ENGINE.get(), pos, state),
    StackedContentsCompatible,
    WorldlyContainer {

    private val ship: ServerShip? get() = (this.level as ServerLevel).getShipManagingPos(this.blockPos)
    val data = KtContainerData()
    private var heatLevel by data
    private var fuelLeft by data
    private var fuelTotal by data
    var fuel: ItemStack = ItemStack.EMPTY
    private var maxEffectiveFuel = 100f - EurekaConfig.SERVER.engineHeatGain
    private var lastFuelValue = 1600; // coal: 1600

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        EngineScreenMenu(containerId, inventory, this)

    override fun getDefaultName(): Component = Component.translatable("gui.vs_eureka.engine")

    private var heat = 0f
    fun tick() {
        if (this.level!!.isClientSide) return

        val isPowered = level!!.hasNeighborSignal(blockPos)
        if (EurekaConfig.SERVER.engineRedstoneBehaviorPause && isPowered) return

        // Disable engine feeding when they are receiving a redstone signal
        if (!isPowered) {
            if (fuelLeft > 0) {

                if (EurekaConfig.SERVER.engineFuelSaving) {
                    if (heat <= maxEffectiveFuel) {
                        heat += scaleEngineHeating(EurekaConfig.SERVER.engineHeatGain)
                        fuelLeft--
                    }
                } else {
                    fuelLeft--

                    if (heat <= maxEffectiveFuel) {
                        heat += scaleEngineHeating(EurekaConfig.SERVER.engineHeatGain)
                    }
                }

                // Refill while burning
                if (!fuel.isEmpty && lastFuelValue <= EurekaConfig.SERVER.engineMinCapacity - fuelLeft) {
                    consumeFuel()
                }
            } else if (!fuel.isEmpty) {
                consumeFuel()
            }
        }

        val prevHeatLevel = heatLevel
        heatLevel = min(ceil(heat * 4f / 100f).toInt(), 4)
        if (prevHeatLevel != heatLevel) {
            level!!.setBlock(blockPos, this.blockState.setValue(HEAT, heatLevel), 11)
        }

        if (heat > 0) {
            val eurekaShipControl = ship?.getAttachment(EurekaShipControl::class.java)
            if (ship != null && eurekaShipControl != null) {
                // Avoid fluctuations in speed
                var effectiveHeat = 1f
                if (heat < maxEffectiveFuel) {
                    effectiveHeat = heat / 100f
                }

                eurekaShipControl.powerLinear += lerp(
                    EurekaConfig.SERVER.enginePowerLinearMin,
                    EurekaConfig.SERVER.enginePowerLinear,
                    effectiveHeat,
                )

                eurekaShipControl.powerAngular += lerp(
                    EurekaConfig.SERVER.enginePowerAngularMin,
                    EurekaConfig.SERVER.enginePowerAngular,
                    effectiveHeat,
                )

                heat -= eurekaShipControl.consumed
            }

            heat = max(heat - scaleEngineCooling(EurekaConfig.SERVER.engineHeatLoss), 0f)
        }
    }

    fun isBurning(): Boolean = fuelLeft > 0

    /**
     * Get fuel value from the item type stored in the engine.
     *
     * @return scaled fuel ticks.
     */
    private fun getScaledFuel(): Int =
        ((FurnaceBlockEntity.getFuel()[fuel.item] ?: 0) * EurekaConfig.SERVER.engineFuelMultiplier).toInt()

    /**
     * Absorb one fuel item into the engine.
     */
    private fun consumeFuel() {

        lastFuelValue = getScaledFuel()

        if (lastFuelValue > 0) {
            if (fuelLeft > 0 && lastFuelValue > EurekaConfig.SERVER.engineMinCapacity - fuelLeft) {
                return
            }

            fuelLeft += lastFuelValue
            fuelTotal = max(lastFuelValue, EurekaConfig.SERVER.engineMinCapacity)

            // Handle items like lava buckets
            if (fuel.item.hasCraftingRemainingItem()) {
                fuel = ItemStack(fuel.item.craftingRemainingItem!!, 1)
            } else {
                removeItem(0, 1)
            }
            setChanged()
        }
    }

    /**
     * Scale given heating [value] based on current heat.
     *
     * @return the scaled value.
     */
    private fun scaleEngineHeating(value: Float): Float =
        (100 * EurekaConfig.SERVER.engineHeatChangeExponent - this.heat * EurekaConfig.SERVER.engineHeatChangeExponent + 1f) * value

    /**
     * Scale given cooling [value] based on current heat.
     *
     * @return the scaled value.
     */
    private fun scaleEngineCooling(value: Float): Float =
        (this.heat * EurekaConfig.SERVER.engineHeatChangeExponent + 1f) * value

    override fun saveAdditional(tag: CompoundTag) {
        tag.put("FuelSlot", fuel.save(CompoundTag()))
        tag.putInt("FuelLeft", fuelLeft)
        tag.putInt("PrevFuelTotal", fuelTotal)
        tag.putFloat("Heat", heat)
        super.saveAdditional(tag)
    }

    override fun load(compoundTag: CompoundTag) {
        fuel = ItemStack.of(compoundTag.getCompound("FuelSlot"))
        fuelLeft = compoundTag.getInt("FuelLeft")
        fuelTotal = compoundTag.getInt("PrevFuelTotal")
        heat = compoundTag.getFloat("Heat")
        super.load(compoundTag)
    }

    // region Container Stuff
    override fun clearContent() {
        fuel = ItemStack.EMPTY
    }

    override fun getContainerSize(): Int = 1

    override fun isEmpty(): Boolean = fuel.isEmpty

    override fun getItem(slot: Int): ItemStack = if (slot == 0) fuel else ItemStack.EMPTY

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        return ContainerHelper.removeItem(listOf(fuel), slot, amount)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        if (slot == 0) fuel = ItemStack.EMPTY
        return ItemStack.EMPTY
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        if (slot == 0) fuel = stack
    }

    override fun stillValid(player: Player): Boolean {
        return if (level!!.getBlockEntity(worldPosition) !== this) {
            false
        } else player.distanceToSqr(
            worldPosition.x.toDouble() + 0.5, worldPosition.y.toDouble() + 0.5, worldPosition.z.toDouble() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: Direction): IntArray = intArrayOf(0)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        direction != Direction.DOWN && canPlaceItem(index, itemStack)

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
        // Allow extraction from slot 0 (fuel slot) when the hopper is below the block entity
        index == 0 && direction == Direction.DOWN && !fuel.isEmpty && !AbstractFurnaceBlockEntity.isFuel(fuel)

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean =
        index == 0 && AbstractFurnaceBlockEntity.isFuel(stack)

    override fun fillStackedContents(helper: StackedContents) = helper.accountStack(fuel)
    // endregion Container Stuff
}
