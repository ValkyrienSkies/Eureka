package org.valkyrienskies.eureka.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.core.registries.Registries
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.loading.FMLEnvironment
import net.minecraftforge.registries.DeferredRegister
import org.valkyrienskies.core.apigame.VSCoreFactory
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.EurekaMod.init
import org.valkyrienskies.eureka.registry.CreativeTabs
import org.valkyrienskies.eureka.forge.registry.FuelRegistryImpl
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
import org.valkyrienskies.mod.forge.common.ForgeHooksImpl
import org.valkyrienskies.mod.forge.common.VSForgeNetworking
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod(EurekaMod.MOD_ID)
class EurekaModForge {
    init {
        runForDist (
            clientTarget = {
                EurekaModForgeClient.registerClient()
            },
            serverTarget = {}
        )
        LOADING_CONTEXT.registerExtensionPoint(
            ConfigScreenHandler.ConfigScreenFactory::class.java
        ) {
            ConfigScreenHandler.ConfigScreenFactory { _: Minecraft?, parent: Screen? ->
                createConfigScreenFor(
                    parent!!,
                    EurekaConfig::class.java,
                )
            }
        }
        FuelRegistryImpl()
        val isClient = FMLEnvironment.dist.isClient
        val vsCore = if (isClient) {
            VSCoreFactory.instance.newVsCoreClient(ForgeHooksImpl)
        } else {
            VSCoreFactory.instance.newVsCoreServer(ForgeHooksImpl)
        }

        VSForgeNetworking.registerPacketHandlers(vsCore.hooks)

        init(vsCore)

        val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EurekaMod.MOD_ID)
        deferredRegister.register("general") {
            CreativeTabs.create()
        }
        deferredRegister.register(getModBus())
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
