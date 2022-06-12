package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.eureka.EurekaScreens
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

class ShipHelmScreenMenu(syncId: Int, playerInv: Inventory, val blockEntity: ShipHelmBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.SHIP_HELM.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    override fun stillValid(player: Player): Boolean {
        return true
    }

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (blockEntity == null) return false

        if (id == 0 && !blockEntity.assembled) {
            blockEntity.onAssemble()
            return true
        }

        return super.clickMenuButton(player, id)
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> ShipHelmScreenMenu = ::ShipHelmScreenMenu
    }
}
