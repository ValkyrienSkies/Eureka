package org.valkyrienskies.eureka.gui.shiphelm

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.valkyrienskies.eureka.EurekaMod

@Environment(EnvType.CLIENT) //Am i allowed to do this in forge?
class ShipHelmScreen(handler: ShipHelmScreenMenu, playerInventory: Inventory, text: Component):
    AbstractContainerScreen<ShipHelmScreenMenu>(handler, playerInventory,  text) {

    private val TEXTURE = ResourceLocation(EurekaMod.MOD_ID, "textures/gui/ship_helm.png")

    init {
        titleLabelX = 120
    }

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        minecraft!!.textureManager.bind(TEXTURE)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight)
    }

    override fun renderLabels(matrixStack: PoseStack, i: Int, j: Int) {
        font.draw(matrixStack, title, titleLabelX.toFloat(), titleLabelY.toFloat(), 0x404040)

        //TODO render stats
    }
}