package org.valkyrienskies.eureka.gui.engine

import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.eureka.EurekaScreens
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity
import org.valkyrienskies.eureka.util.KtContainerData
import org.valkyrienskies.eureka.util.inventorySlots

class EngineScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: EngineBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.ENGINE.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    private val container = blockEntity ?: SimpleContainer(1)
    private val data = blockEntity?.data?.clone() ?: KtContainerData()
    var heatLevel by data
    var coalLevel by data

    init {
        inventorySlots(::addSlot, playerInv)
        addDataSlots(data)

        // Add the fuel slot
        addSlot(FuelSlot(container, 0, 80, 57))
    }

    override fun stillValid(player: Player?): Boolean = true

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> EngineScreenMenu = ::EngineScreenMenu
    }
}
