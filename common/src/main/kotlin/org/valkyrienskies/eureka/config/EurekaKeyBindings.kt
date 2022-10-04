package org.valkyrienskies.mod.common.config

import com.mojang.blaze3d.platform.InputConstants
import me.shedaniel.architectury.registry.KeyBindings
import net.minecraft.client.KeyMapping
import java.util.function.Consumer
import java.util.function.Supplier

object EurekaKeyBindings {
    val CRUISE_MAPPING = KeyMapping(
            "key.examplemod.custom_key",  // The translation key of the name shown in the Controls screen
            InputConstants.Type.KEYSYM,  // This key mapping is for Keyboards by default
            88,  // The default keycode
            "category.valkyrienskies.driving" // The category translation key used to categorize in the Controls screen
    )

    fun register(){
        KeyBindings.registerKeyBinding(CRUISE_MAPPING);
    }

}
