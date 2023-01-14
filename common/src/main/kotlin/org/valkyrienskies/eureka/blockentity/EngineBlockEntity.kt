package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
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
import org.valkyrienskies.core.impl.api.ServerShipProvider
import org.valkyrienskies.core.impl.api.shipValue
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaProperties.HEAT
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.mod.common.getShipManagingPos
import kotlin.math.ceil

class EngineBlockEntity(pos: BlockPos, state: BlockState) :
    BaseContainerBlockEntity(EurekaBlockEntities.ENGINE.get(), pos, state),
    ServerShipProvider,
    StackedContentsCompatible,
    WorldlyContainer {

    override val ship: ServerShip? get() = (this.level as ServerLevel).getShipManagingPos(this.blockPos)
    private val eurekaShipControl by shipValue<EurekaShipControl>()
    val data = KtContainerData()
    var heatLevel by data
    var fuelLeft by data
    var fuelTotal by data
    private var fuel: ItemStack = ItemStack.EMPTY

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        EngineScreenMenu(containerId, inventory, this)

    override fun getDefaultName(): Component = TranslatableComponent("gui.vs_eureka.engine")

    private var heat = 0f
    fun tick() {
        if (!this.level!!.isClientSide) {
            // Disable engines when they are receiving a redstone signal
            if (level!!.hasNeighborSignal(blockPos)) {
                heatLevel = 0
                level!!.setBlock(blockPos, this.blockState.setValue(HEAT, 0), 11)
                return
            }

            if (this.fuelLeft > 0) {
                this.fuelLeft--

                if (this.heat < 100f) {
                    this.heat += EurekaConfig.SERVER.engineHeatGain
                }
            } else if (!fuel.isEmpty && this.heat < 100f) {
                fuelTotal = (FurnaceBlockEntity.getFuel()[fuel.item] ?: 0) * 2
                fuelLeft = fuelTotal
                removeItem(0, 1)
                setChanged()
            }

            val prevHeatLevel = heatLevel
            heatLevel = min(ceil(heat * 4f / 100f).toInt(), 4)
            if (prevHeatLevel != heatLevel) {
                level!!.setBlock(blockPos, this.blockState.setValue(HEAT, heatLevel), 11)
            }

            if (heat > 0 && ship != null && eurekaShipControl != null) {
                eurekaShipControl!!.power += lerp(
                    heat / 100f,
                    EurekaConfig.SERVER.minEnginePower,
                    EurekaConfig.SERVER.enginePower
                )

                heat -= eurekaShipControl!!.consumed
            }

            if (heat > 0) {
                heat -= min(EurekaConfig.SERVER.engineHeatLoss, heat)
            }
        }
    }

    fun isBurning() = fuelLeft > 0

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

    override fun getItem(slot: Int): ItemStack =
        if (slot == 0) fuel else ItemStack.EMPTY

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
            worldPosition.x.toDouble() + 0.5,
            worldPosition.y.toDouble() + 0.5,
            worldPosition.z.toDouble() + 0.5
        ) <= 64.0
    }

    override fun getSlotsForFace(side: Direction?): IntArray =
        if (side == Direction.DOWN) intArrayOf() else intArrayOf(0)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        direction != Direction.DOWN && canPlaceItem(index, itemStack)

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack?, direction: Direction?): Boolean = false

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean =
        index == 0 && AbstractFurnaceBlockEntity.isFuel(stack)

    override fun fillStackedContents(helper: StackedContents) = helper.accountStack(fuel)
    // endregion Container Stuff
}
