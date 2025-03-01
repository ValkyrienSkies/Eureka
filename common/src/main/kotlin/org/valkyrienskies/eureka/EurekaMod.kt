package org.valkyrienskies.eureka

import org.valkyrienskies.core.apigame.VSCore
import org.valkyrienskies.mod.common.ValkyrienSkiesMod

object EurekaMod {
    const val MOD_ID = "vs_eureka"

    @JvmStatic
    lateinit var vsCore: VSCore

    @JvmStatic
    fun init(core: VSCore) {
        this.vsCore = core
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
        EurekaWeights.register()
        core.registerConfigLegacy("vs_eureka", EurekaConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
