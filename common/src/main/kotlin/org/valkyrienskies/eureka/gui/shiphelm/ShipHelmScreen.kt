package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text

class ShipHelmScreen(handler: ShipHelmScreenHandler, playerInventory: PlayerInventory, text: Text):
    HandledScreen<ShipHelmScreenHandler>(handler, playerInventory,  text) {

    override fun drawBackground(matrixStack: MatrixStack?, f: Float, i: Int, j: Int) {
        TODO("Not yet implemented")
    }
}