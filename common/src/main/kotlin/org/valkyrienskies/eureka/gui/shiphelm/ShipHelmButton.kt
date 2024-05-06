package org.valkyrienskies.eureka.gui.shiphelm

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.Button
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.Component
import net.minecraft.util.FormattedCharSequence

class ShipHelmButton(x: Int, y: Int, text: Component, private val font: Font, onPress: OnPress) :
    Button(x, y, 156, 23, text, onPress) {

    var isPressed = false

    init {
        active = true
    }

    override fun renderButton(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTicks: Float) {
        if (!isHovered) isPressed = false

        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, ShipHelmScreen.TEXTURE)

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.enableDepthTest()

        if (this.isPressed || !this.active) {
            this.blit(poseStack, x, y, BUTTON_P_X, BUTTON_P_Y, width, height)
        } else if (this.isHovered) {
            this.blit(poseStack, x, y, BUTTON_H_X, BUTTON_H_Y, width, height)
        }

        val color = 0x404040
        val formattedCharSequence: FormattedCharSequence = message.visualOrderText
        font.draw(
            poseStack,
            formattedCharSequence,
            ((x + width / 2) - font.width(formattedCharSequence) / 2).toFloat(),
            (y + (height - 8) / 2).toFloat(),
            color
        )
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        isPressed = true
        super.onClick(mouseX, mouseY)
    }

    override fun onRelease(mouseX: Double, mouseY: Double) {
        isPressed = false
    }

    companion object {
        private const val BUTTON_H_X = 0
        private const val BUTTON_H_Y = 166
        private const val BUTTON_P_X = 0
        private const val BUTTON_P_Y = 189
    }
}
