package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import org.valkyrienskies.eureka.EurekaScreens

class ShipHelmScreenHandler(syncId: Int, playerInv: PlayerInventory): ScreenHandler(EurekaScreens.SHIP_HELM.get(), syncId) {

    override fun canUse(playerEntity: PlayerEntity): Boolean {
        return true
    }
}