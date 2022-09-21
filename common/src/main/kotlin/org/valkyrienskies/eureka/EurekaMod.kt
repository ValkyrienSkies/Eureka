package org.valkyrienskies.eureka

import me.shedaniel.architectury.event.events.LifecycleEvent
import me.shedaniel.architectury.event.events.TickEvent
import org.valkyrienskies.core.config.VSConfigClass
import org.valkyrienskies.eureka.util.ShipAssembler

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
        TickEvent.SERVER_POST.register {
            ShipAssembler.tickAssemblyTasks()
        }
        LifecycleEvent.SERVER_STOPPING.register {
            ShipAssembler.clearAssemblyTasks()
        }
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
    }
}
