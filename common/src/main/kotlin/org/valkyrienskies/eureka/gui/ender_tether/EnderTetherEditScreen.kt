package org.valkyrienskies.eureka.gui.ender_tether

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiComponent
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.EditBox
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.TranslatableComponent
import org.valkyrienskies.core.impl.networking.simple.sendToServer
import org.valkyrienskies.eureka.item.EnderTether
import org.valkyrienskies.eureka.networking.PacketSetTetherDistance

class EnderTetherEditScreen(private val initialTetherDistance: Double) : Screen(SCREEN_TITLE) {

    private lateinit var numberField: EditBox
    private lateinit var doneButton: Button

    override fun init() {
        // Put this in the center of the screen
        numberField = EditBox(
            this.font,
            (this.width - NUM_BOX_WIDTH) / 2,
            (this.height - NUM_BOX_HEIGHT) / 2,
            NUM_BOX_WIDTH,
            NUM_BOX_HEIGHT,
            EDIT_BOX_DESCRIPTION,
        ).apply {
            setMaxLength(10)
            setFilter { s -> s.matches(REGEX) }
            value = if (::numberField.isInitialized) {
                // Copy val from prev box
                numberField.value
            } else {
                initialTetherDistance.toString()
            }
        }
        doneButton = Button(
            width / 2 - 50,
            height / 2 + 30,
            100,
            20,
            DONE_BOX_TEXT,
        ) {
            sendTetherDistanceToServer()
            onClose()
        }

        addRenderableWidget(numberField)
        addRenderableWidget(doneButton)
        setInitialFocus(numberField)
    }

    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        // Dim the outside of the gui
        renderBackground(poseStack)
        // Render the background texture
        RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE)
        val x = (width - 176) / 2
        val y = (height - 166) / 2
        this.blit(poseStack, x, y, 0, 0, 176, 166)

        // Render the input field and button
        super.render(poseStack, mouseX, mouseY, partialTick)

        // Render the title
        drawCenteredString(poseStack, font, title.string, width / 2, 20, 0xFFFFFF)

        // Render the invalid text
        if (!isInputValid()) {
            drawCenteredString(
                poseStack,
                font,
                OUT_OF_RANGE_TEXT.string + " {${EnderTether.MIN_TETHER_DISTANCE}, ${EnderTether.MAX_TETHER_DISTANCE}}",
                this.width / 2,
                this.height / 2 + 15,
                0xFF008D,
            )
        }

        // Render the edit box description
        drawCenteredString(
            poseStack,
            this.font,
            EDIT_BOX_DESCRIPTION,
            numberField.x + numberField.width / 2,
            numberField.y - 10,
            10526880,
        )
    }

    override fun tick() {
        numberField.tick()
    }

    override fun isPauseScreen(): Boolean {
        return false
    }

    private fun sendTetherDistanceToServer() {
        // Handle the number entered (you can add logic to process the input here)
        if (isInputValid()) {
            val enteredNumber = numberField.value.toDouble()
            PacketSetTetherDistance(enteredNumber, minecraft!!.player!!.usedItemHand).sendToServer()
        }
    }

    private fun isInputValid(): Boolean {
        val enteredNumber = numberField.value.toDouble()
        return enteredNumber == enteredNumber.coerceIn(EnderTether.MIN_TETHER_DISTANCE, EnderTether.MAX_TETHER_DISTANCE)
    }

    override fun onClose() {
        super.onClose()
        minecraft!!.setScreen(null)
    }

    companion object {
        private val REGEX = Regex("^(\\d*)(\\.\\d+)?\$")
        // TODO: Custom background texture
        private val BACKGROUND_TEXTURE =
            GuiComponent.BACKGROUND_LOCATION // ResourceLocation("mymod", "textures/gui/number_input.png")
        // TODO: Add translation keys
        private val SCREEN_TITLE = TranslatableComponent("Ender Tether")
        private val EDIT_BOX_DESCRIPTION = TranslatableComponent("Tether Distance")
        private val DONE_BOX_TEXT = TranslatableComponent("Done")
        private val OUT_OF_RANGE_TEXT = TranslatableComponent("Out Of Range")
        private const val NUM_BOX_WIDTH = 100
        private const val NUM_BOX_HEIGHT = 20
    }
}
