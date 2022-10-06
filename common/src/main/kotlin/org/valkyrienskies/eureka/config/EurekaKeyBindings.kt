package org.valkyrienskies.mod.common.config

import com.mojang.blaze3d.platform.InputConstants
import me.shedaniel.architectury.event.events.client.ClientTickEvent
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import org.joml.Vector3f
import org.valkyrienskies.core.networking.simple.sendToServer
import org.valkyrienskies.mod.common.networking.PacketPlayerCruise
import org.valkyrienskies.mod.common.networking.PacketPlayerDriving

object EurekaKeyBindings {
    val CRUISE_MAPPING = KeyMapping(
            "key.examplemod.custom_key",  // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
            88,  // The default keycode
            "category.valkyrienskies.driving" // The category translation key used to categorize in the Controls screen
    )

    fun register(){
        KeyBindings.registerKeyBinding(CRUISE_MAPPING);
        ClientTickEvent.CLIENT_POST.register { minecraft ->
            while (CRUISE_MAPPING.consumeClick()) {
                PacketPlayerCruise(true).sendToServer()
            }
        }
    }

}
