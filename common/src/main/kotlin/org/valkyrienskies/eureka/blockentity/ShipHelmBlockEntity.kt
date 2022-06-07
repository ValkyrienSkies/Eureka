package org.valkyrienskies.eureka.blockentity

import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.NamedScreenHandlerFactory
import net.minecraft.screen.ScreenHandler
import net.minecraft.text.Text
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenHandler

class ShipHelmBlockEntity: BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), NamedScreenHandlerFactory {

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }

    override fun createMenu(id: Int, playerInventory: PlayerInventory, player: PlayerEntity): ScreenHandler {
        return ShipHelmScreenHandler(id, playerInventory)
    }

    override fun getDisplayName(): Text {
        return Text.of("Ship Helm")
    }
}