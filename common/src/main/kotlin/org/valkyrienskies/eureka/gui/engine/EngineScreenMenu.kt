package org.valkyrienskies.eureka.gui.engine

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.eureka.EurekaScreens
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity

class EngineScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: EngineBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.ENGINE.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    override fun stillValid(player: Player?): Boolean = true

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> EngineScreenMenu = ::EngineScreenMenu
    }
}
