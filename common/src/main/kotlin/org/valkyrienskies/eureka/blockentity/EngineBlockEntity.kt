package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity
import net.minecraft.world.level.block.entity.TickableBlockEntity
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.mod.api.BlockEntityShipProvider

class EngineBlockEntity :
    BaseContainerBlockEntity(EurekaBlockEntities.ENGINE.get()),
    BlockEntityShipProvider,
    TickableBlockEntity,
    WorldlyContainer {

    override var ship: Ship? = null
    val data = KtContainerData()
    var heatLevel by data
    var coalLevel by data
    private var fuel: ItemStack = ItemStack.EMPTY

    override fun createMenu(containerId: Int, inventory: Inventory): AbstractContainerMenu =
        EngineScreenMenu(containerId, inventory, this)

    override fun getDefaultName(): Component = Component.nullToEmpty("Ship Engine")

    companion object {
        val supplier = { EngineBlockEntity() }
    }

    var counter = 0

    override fun tick() {
        if (++counter > 40) {
            counter = 0
            if (++heatLevel > 4) {
                heatLevel = 0
                if (++coalLevel > 4) {
                    coalLevel = 0
                }
            }
        }
    }

    override fun clearContent() {
        fuel = ItemStack.EMPTY
    }

    override fun getContainerSize(): Int = 1

    override fun isEmpty(): Boolean = fuel.isEmpty

    override fun getItem(slot: Int): ItemStack =
        if (slot == 0) fuel else ItemStack.EMPTY

    override fun removeItem(slot: Int, amount: Int): ItemStack =
        if (slot == 0 && fuel.count > amount) {
            fuel.count = fuel.count - amount; fuel
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
}
