package org.valkyrienskies.eureka.gui.shiphelm

import com.mojang.blaze3d.systems.RenderSystem
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.valkyrienskies.eureka.EurekaMod

@Environment(EnvType.CLIENT) //Am i allowed to do this in forge?
class ShipHelmScreen(handler: ShipHelmScreenHandler, playerInventory: PlayerInventory, text: Text):
    HandledScreen<ShipHelmScreenHandler>(handler, playerInventory,  text) {

    private val TEXTURE = Identifier(EurekaMod.MOD_ID, "textures/gui/ship_helm.png")

    init {
        titleX = 120
    }

    override fun drawBackground(matrixStack: MatrixStack?, f: Float, i: Int, j: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        client!!.textureManager.bindTexture(TEXTURE)
        drawTexture(matrixStack, x, y, 0, 0, backgroundWidth, backgroundHeight)
    }

    override fun drawForeground(matrixStack: MatrixStack?, i: Int, j: Int) {
        textRenderer.draw(matrixStack, title, titleX.toFloat(), titleY.toFloat(), 0x404040)

        //TODO render stats and buttons
    }
}