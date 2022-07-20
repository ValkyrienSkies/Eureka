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

@Environment(EnvType.CLIENT)
class ShipHelmScreen(handler: ShipHelmScreenMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<ShipHelmScreenMenu>(handler, playerInventory, text) {

    init {
        titleLabelX = 120
    }

    override fun init() {
        super.init()
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        addButton(
            ShipHelmButton(x + BUTTON_1_X, y + BUTTON_1_Y, Component.nullToEmpty("Assemble"), font) {
                minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 0)
            }
        )
        addButton(
            ShipHelmButton(x + BUTTON_2_X, y + BUTTON_2_Y, Component.nullToEmpty("Go Crazy"), font) {
                minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 1)
            }
        )
        addButton(
            ShipHelmButton(x + BUTTON_3_X, y + BUTTON_3_Y, Component.nullToEmpty("Align"), font) {
                minecraft!!.gameMode!!.handleInventoryButtonClick(menu.containerId, 2)
            }
        )
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

        // TODO render stats
    }

    // mojank doesn't check mouse release for their widgets for some reason
    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean {
        isDragging = false
        if (getChildAt(mouseX, mouseY).filter { it.mouseReleased(mouseX, mouseY, button) }.isPresent) return true

        return super.mouseReleased(mouseX, mouseY, button)
    }

    companion object { // TEXTURE DATA
        internal val TEXTURE = ResourceLocation(EurekaMod.MOD_ID, "textures/gui/ship_helm.png")

        private const val BUTTON_1_X = 10
        private const val BUTTON_1_Y = 73
        private const val BUTTON_2_X = 10
        private const val BUTTON_2_Y = 103
        private const val BUTTON_3_X = 10
        private const val BUTTON_3_Y = 133
    }
}
