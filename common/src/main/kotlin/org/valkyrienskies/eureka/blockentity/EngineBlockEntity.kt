package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
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
import net.minecraft.world.level.block.entity.TickableBlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.mod.api.ShipBlockEntity

const val MAX_HEAT = 2000

class EngineBlockEntity :
    BaseContainerBlockEntity(EurekaBlockEntities.ENGINE.get()),
    ShipBlockEntity,
    TickableBlockEntity,
    StackedContentsCompatible,
    WorldlyContainer {

    override var ship: Ship? = null
    val data = KtContainerData()
    var heatLevel by data
    var fuelLevel by data
    private var fuel: ItemStack = ItemStack.EMPTY

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        EngineScreenMenu(containerId, inventory, this)

    override fun getDefaultName(): Component = Component.nullToEmpty("Ship Engine")

    private var fuelLeft = 0
    private var prevFuelTotal = 0
    private var heat = 0

    override fun tick() {
        if (!this.level!!.isClientSide) {

            if (this.fuelLeft > 0) {
                this.fuelLeft--

                if (this.heat < MAX_HEAT) {
                    this.heat++
                }
            } else if (!fuel.isEmpty && this.heat < MAX_HEAT) {
                prevFuelTotal = (FurnaceBlockEntity.getFuel()[fuel.item] ?: 0) * 2
                fuelLeft = prevFuelTotal
                removeItem(0, 1)
                setChanged()
            }

            fuelLevel = ((fuelLeft.toFloat() / prevFuelTotal.toFloat()) * 4.5f).toInt()
            heatLevel = (heat * 4) / MAX_HEAT
        }
    }

    fun isBurning() = fuelLeft > 0

    override fun save(tag: CompoundTag): CompoundTag {
        tag.put("FuelSlot", fuel.save(CompoundTag()))
        tag.putInt("FuelLeft", fuelLeft)
        tag.putInt("PrevFuelTotal", prevFuelTotal)
        tag.putInt("Heat", heat)
        return super.save(tag)
    }

    override fun load(blockState: BlockState, compoundTag: CompoundTag) {
        fuel = ItemStack.of(compoundTag.getCompound("FuelSlot"))
        fuelLeft = compoundTag.getInt("FuelLeft")
        prevFuelTotal = compoundTag.getInt("PrevFuelTotal")
        heat = compoundTag.getInt("Heat")
        super.load(blockState, compoundTag)
    }

    override fun clearContent() {
        fuel = ItemStack.EMPTY
    }

    override fun getContainerSize(): Int = 1

    override fun isEmpty(): Boolean = fuel.isEmpty

    override fun getItem(slot: Int): ItemStack =
        if (slot == 0) fuel else ItemStack.EMPTY

    override fun removeItem(slot: Int, amount: Int): ItemStack =
        if (slot == 0) {
            if (fuel.count > amount) {
                fuel.count = fuel.count - amount
            } else fuel = ItemStack.EMPTY

            fuel
        } else ItemStack.EMPTY

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

    override fun getSlotsForFace(side: Direction): IntArray =
        if (side == Direction.DOWN) intArrayOf() else intArrayOf(0)

    override fun canPlaceItemThroughFace(index: Int, itemStack: ItemStack, direction: Direction?): Boolean =
        direction != Direction.DOWN && canPlaceItem(index, itemStack)

    override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean = false

    override fun canPlaceItem(index: Int, stack: ItemStack): Boolean =
        index == 0 && AbstractFurnaceBlockEntity.isFuel(stack)

    override fun fillStackedContents(helper: StackedContents) = helper.accountStack(fuel)

    companion object {
        val supplier = { EngineBlockEntity() }
    }
}
