package org.valkyrienskies.eureka

import me.shedaniel.architectury.event.events.LifecycleEvent
import me.shedaniel.architectury.event.events.TickEvent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import org.valkyrienskies.core.config.VSConfigClass
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.config.EurekaKeyBindings
import org.valkyrienskies.mod.common.entity.EurekaShipMountingEntity
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.networking.EurekaGamePackets

object EurekaMod {
    const val MOD_ID = "vs_eureka"
    lateinit var EUREKA_SHIP_MOUNTING_ENTITY_TYPE: EntityType<ShipMountingEntity>

    @JvmStatic
    fun init() {
        EurekaBlocks.register()
        EurekaBlockEntities.register()
        EurekaItems.register()
        EurekaScreens.register()
        EurekaEntities.register()
        EurekaWeights.register()
        EurekaGamePackets.register()
        EurekaGamePackets.registerHandlers()
        VSConfigClass.registerConfig("vs_eureka", EurekaConfig::class.java)

        TickEvent.SERVER_POST.register {
            ShipAssembler.tickAssemblyTasks()
        }

        LifecycleEvent.SERVER_STOPPING.register {
            ShipAssembler.clearAssemblyTasks()
        }
        EUREKA_SHIP_MOUNTING_ENTITY_TYPE = EntityType.Builder.of(
                ::EurekaShipMountingEntity,
                MobCategory.MISC
        ).sized(.3f, .3f)
                .build(ResourceLocation(MOD_ID, "eureka_ship_mounting_entity").toString())
    }

    @JvmStatic
    fun initClient() {
        EurekaClientScreens.register()
        EurekaKeyBindings.register()
    }
}
