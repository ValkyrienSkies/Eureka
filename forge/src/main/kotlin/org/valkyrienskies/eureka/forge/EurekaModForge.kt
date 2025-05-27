package org.valkyrienskies.eureka.forge

import net.minecraft.core.registries.Registries
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.registries.DeferredRegister
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.EurekaMod.init
import org.valkyrienskies.eureka.forge.registry.FuelRegistryImpl
import org.valkyrienskies.eureka.registry.CreativeTabs
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
import thedarkcolour.kotlinforforge.neoforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.neoforge.forge.runForDist

@Mod(EurekaMod.MOD_ID)
object EurekaModForge {
    init {
        runForDist (
            clientTarget = {
                EurekaModForgeClient.registerClient()
            },
            serverTarget = {}
        )
        LOADING_CONTEXT.registerExtensionPoint(IConfigScreenFactory::class.java) {
            IConfigScreenFactory { _, parent ->
                createConfigScreenFor(
                    parent,
                    EurekaConfig::class.java,
                )
            }
        }

        FuelRegistryImpl()
        init()

        val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EurekaMod.MOD_ID)
        deferredRegister.register("general") { ->
            CreativeTabs.create()
        }
        deferredRegister.register(getModBus())
    }

    fun getModBus(): IEventBus = MOD_BUS
}
