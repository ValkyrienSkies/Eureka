package org.valkyrienskies.eureka.gui.engine

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import org.valkyrienskies.eureka.EurekaMod

@Environment(EnvType.CLIENT) // Am i allowed to do this in forge?
class EngineScreen(handler: EngineScreenMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<EngineScreenMenu>(handler, playerInventory, text) {

    // The texture is 512 so every coord is 2 pixels big
    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        minecraft!!.textureManager.bind(TEXTURE)
        val x = (width - imageWidth) / 4
        val y = (height - imageHeight) / 4

        menu as EngineScreenMenu

        matrixStack.pushPose()
        matrixStack.scale(2f, 2f, 2F)
        // Draw the container background
        val (containerX, containerY) = if (menu.heatLevel > 1)
            Pair(HEATED_CONTAINER_X, HEATED_CONTAINER_Y)
        else
            Pair(CONTAINER_X, CONTAINER_Y)

        blit(matrixStack, x + FIRE_HOLE_X, y + FIRE_HOLE_Y, containerX, containerY, FIRE_HOLE_WIDTH, FIRE_HOLE_HEIGHT)

        // region COALS
        // Draw the coal
        // TODO
        // endregion


        // Draw the glass background
        val (glassX, glassY) = if (menu.heatLevel > 3)
            Pair(HEATED_GLASS_X, HEATED_GLASS_Y)
        else
            Pair(GLASS_X, GLASS_Y)

        blit(matrixStack, x + FIRE_HOLE_X, y + FIRE_HOLE_Y, glassX, glassY, FIRE_HOLE_WIDTH, FIRE_HOLE_HEIGHT)

        // Draw the inventory
        blit(matrixStack, x, y, 0, 0, imageWidth / 2, imageHeight / 2)
        matrixStack.popPose()
    }

    override fun renderLabels(poseStack: PoseStack?, mouseX: Int, mouseY: Int) {
        // super.renderLabels(poseStack, mouseX, mouseY)
    }

    companion object { // TEXTURE DATA
        internal val TEXTURE = ResourceLocation(EurekaMod.MOD_ID, "textures/gui/engine.png")

        private const val FIRE_HOLE_X = 10 / 2
        private const val FIRE_HOLE_Y = 8 / 2

        private const val FIRE_HOLE_WIDTH = 156 / 2
        private const val FIRE_HOLE_HEIGHT = 68 / 2

        private const val HEATED_GLASS_X = 10 / 2
        private const val HEATED_GLASS_Y = 172 / 2
        private const val GLASS_X = 10 / 2
        private const val GLASS_Y = 244 / 2

        private const val HEATED_CONTAINER_X = 10 / 2
        private const val HEATED_CONTAINER_Y = 390 / 2
        private const val CONTAINER_X = 10 / 2
        private const val CONTAINER_Y = 318 / 2

        private const val COAL_4_MULT = 1.5f
        private const val COAL_3_MULT = 1.2f
        private const val COAL_2_MULT = 1.1f
        private const val COAL_1_MULT = 1f

        // TODO fill in actual pixel coords
        private const val COAL_4_X = 10 / 2
        private const val COAL_4_Y = 10 / 2
        private const val COAL_3_X = 10 / 2
        private const val COAL_3_Y = 10 / 2
        private const val COAL_2_X = 10 / 2
        private const val COAL_2_Y = 10 / 2
        private const val COAL_1_X = 10 / 2
        private const val COAL_1_Y = 10 / 2
        private const val COAL_WIDTH = 100 / 2
        private const val COAL_4_HEIGHT = 50 / 2
        private const val COAL_3_HEIGHT = 50 / 2
        private const val COAL_2_HEIGHT = 50 / 2
        private const val COAL_1_HEIGHT = 50 / 2
    }
}
