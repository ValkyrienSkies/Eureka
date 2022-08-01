package org.valkyrienskies.eureka

import org.valkyrienskies.core.config.VSConfigClass

object EurekaMod {
    const val MOD_ID = "vs_eureka"

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
        VSConfigClass.registerConfig("vs_eureka", EurekaConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
