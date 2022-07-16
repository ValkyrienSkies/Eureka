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

    override fun renderBg(matrixStack: PoseStack, partialTicks: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f)
        minecraft!!.textureManager.bind(TEXTURE)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        blitOffset = 2
        blit(matrixStack, x, y, 0, 0, imageWidth, imageHeight)

        menu as EngineScreenMenu

        // Draw the coals
        blitOffset = 1
        val coalState = menu.coalLevel
        if (coalState > 0) {
            val xx = coalState - 1
            blit(
                matrixStack,
                x + FIRE_HOLE_X,
                y + FIRE_HOLE_Y,
                COAL_X + (xx * FIRE_HOLE_SIZE),
                COAL_Y,
                FIRE_HOLE_SIZE,
                FIRE_HOLE_SIZE
            )
        }

        // Draw the orangyness based on the heat
        blitOffset = 0
        val heatState = menu.heatLevel
        if (heatState > 0) {
            val xx = heatState - 1
            blit(
                matrixStack,
                x + FIRE_HOLE_X,
                y + FIRE_HOLE_Y,
                HEAT_X + (xx * FIRE_HOLE_SIZE),
                HEAT_Y,
                FIRE_HOLE_SIZE,
                FIRE_HOLE_SIZE
            )
        }
    }

    companion object { // TEXTURE DATA
        internal val TEXTURE = ResourceLocation(EurekaMod.MOD_ID, "textures/gui/engine.png")

        private const val FIRE_HOLE_X = 78
        private const val FIRE_HOLE_Y = 29
        private const val FIRE_HOLE_SIZE = 20

        private const val HEAT_X = 176
        private const val HEAT_Y = 0
        private const val COAL_X = 176
        private const val COAL_Y = 20
    }
}
