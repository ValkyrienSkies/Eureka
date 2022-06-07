package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.eureka.EurekaScreens

class ShipHelmScreenMenu(syncId: Int, playerInv: Inventory):
    AbstractContainerMenu(EurekaScreens.SHIP_HELM.get(), syncId) {

    override fun stillValid(player: Player): Boolean {
        return true
    }
}