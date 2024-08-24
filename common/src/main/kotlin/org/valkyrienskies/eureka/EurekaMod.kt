package org.valkyrienskies.eureka

import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.eureka.networking.EurekaGamePackets

object EurekaMod {
    const val MOD_ID = "vs_eureka"

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
        EurekaWeights.register()
        VSConfigClass.registerConfig("vs_eureka", EurekaConfig::class.java)
        EurekaGamePackets.register()
        EurekaGamePackets.registerHandlers()
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
