package org.valkyrienskies.eureka.gui.ender_tether

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent

// TODO: Add translation key for this
class EnderTetherEditScreen : Screen(TranslatableComponent("Ender Tether")) {

    private val numberField: EditBox =
        EditBox(this.font, this.width / 2 - 50, this.height / 2 - 10, 100, 20, TextComponent("Enter a number")).apply {
            setMaxLength(10)
            setFilter { s -> s.matches(REGEX) }
        }

    private val doneButton: Button =
        Button(width / 2 - 50, height / 2 + 30, 100, 20, TextComponent("Done")) { onClose() }

    init {
        addRenderableWidget(numberField)
        addRenderableWidget(doneButton)
        setInitialFocus(numberField)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Render the background texture
        // Render the background texture
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE)
        val x = (width - 176) / 2
        val y = (height - 166) / 2
        this.blit(poseStack, x, y, 0, 0, 176, 166)

        // Render the input field and button

        // Render the input field and button
        super.render(poseStack, mouseX, mouseY, partialTick)

        // Render the title
        drawCenteredString(poseStack, font, title.string, width / 2, 20, 0xFFFFFF)
    }

    override fun tick() {
        numberField.tick()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    override fun onClose() {
        super.onClose()
        minecraft!!.setScreen(null)
        // Handle the number entered (you can add logic to process the input here)
        val enteredNumber = numberField.value
        // TODO: Do something with the entered number, e.g., send to server, validate, etc.
    }

    companion object {
        private val REGEX = Regex("\\d*")
        private val BACKGROUND_TEXTURE = GuiComponent.BACKGROUND_LOCATION // ResourceLocation("mymod", "textures/gui/number_input.png")
    }
}
