package org.valkyrienskies.eureka

import org.valkyrienskies.core.impl.config.VSConfigClass


object EurekaMod {
    const val MOD_ID = "vs_eureka"
    private var isTick1 = false

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
        EurekaWeights.register()
        VSConfigClass.registerConfig("vs_eureka", EurekaConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
