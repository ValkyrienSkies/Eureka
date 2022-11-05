package org.valkyrienskies.eureka.gui.engine

import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.FurnaceFuelSlot.isBucket
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity
import org.valkyrienskies.eureka.EurekaScreens
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.eureka.util.inventorySlots

class EngineScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: EngineBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.ENGINE.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    private val container: Container = blockEntity ?: SimpleContainer(1)
    private val data = blockEntity?.data?.clone() ?: KtContainerData()
    var heatLevel by data
    var fuelLeft by data
    var fuelTotal by data

    init {
        // Add the fuel slot
        addSlot(FuelSlot(container, 0, 80, 57))

        // Make inventory
        inventorySlots(::addSlot, playerInv)

        // Synced data
        addDataSlots(data)
    }

    override fun stillValid(player: Player): Boolean = container.stillValid(player)

    override fun quickMoveStack(player: Player, index: Int): ItemStack {
        val slot = this.slots[index]
        if (slot != null && slot.hasItem() && (AbstractFurnaceBlockEntity.isFuel(slot.item) || isBucket(slot.item))) {
            if (index != 0) {
                this.moveItemStackTo(slot.item, 0, 1, false)
                slot.setChanged()
            } else {
                slot.onTake(player, slot.item)
                this.moveItemStackTo(slot.item, 1, 37, true)
            }
        }

        return ItemStack.EMPTY
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> EngineScreenMenu = ::EngineScreenMenu
    }
}
