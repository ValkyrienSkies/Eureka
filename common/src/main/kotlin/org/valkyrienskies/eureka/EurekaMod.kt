package org.valkyrienskies.eureka

object EurekaMod {
    const val MOD_ID = "vs_eureka"

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
