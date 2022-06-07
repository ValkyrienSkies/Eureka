package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.Registries
import net.minecraft.util.LazyLoadedValue

object EurekaMod {
    const val MOD_ID = "vs_eureka"

    val REGISTRIES = LazyLoadedValue {
        Registries.get(
            MOD_ID
        )
    }

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
    }

    @JvmStatic
    fun initClient() {
        EurekaScreens.registerClient()
    }
}