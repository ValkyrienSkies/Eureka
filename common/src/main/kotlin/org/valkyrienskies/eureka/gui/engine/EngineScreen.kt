package org.valkyrienskies.eureka.gui.engine

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

@Environment(EnvType.CLIENT) // Am i allowed to do this in forge?
class EngineScreen(handler: EngineScreenMenu, playerInventory: Inventory, text: Component) :
    AbstractContainerScreen<EngineScreenMenu>(handler, playerInventory, text) {

    override fun renderBg(poseStack: PoseStack?, partialTicks: Float, mouseX: Int, mouseY: Int) {
        // TODO("Not yet implemented")
    }
}