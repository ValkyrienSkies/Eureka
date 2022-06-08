package org.valkyrienskies.eureka.gui.shiphelm

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.util.Mth

class ShipHelmButton(x: Int, y: Int, text: Component, onPress: OnPress) :
    Button(x, y, 156, 23, text, onPress) {

    var isPressed = false

    init {
        active = true
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!isHovered()) isPressed = false

        val minecraft = Minecraft.getInstance()
        val font = minecraft.font
        minecraft.textureManager.bind(ShipHelmScreen.TEXTURE)
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, alpha)

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()

        if (!this.active || this.isHovered()) {
            if (this.isPressed) {
                this.blit(poseStack, x, y, BUTTON_P_X, BUTTON_P_Y, width, height)
            } else if (this.isHovered()) {
                this.blit(poseStack, x, y, BUTTON_H_X, BUTTON_H_Y, width, height)
            }
            if (!this.active) {
                println("Wierd")
            }
        }

        val j = if (active) 0xFFFFFF else 0xA0A0A0
        drawCenteredString(
            poseStack, font,
            message, x + width / 2, y + (height - 8) / 2, j or Mth.ceil(alpha * 255.0f) shl 24
        )
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        isPressed = true
        super.onClick(mouseX, mouseY)
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        isPressed = false
        onPress()
    }

    companion object {
        private const val BUTTON_H_X = 0
        private const val BUTTON_H_Y = 166
        private const val BUTTON_P_X = 0
        private const val BUTTON_P_Y = 189
    }
}