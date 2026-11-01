package org.valkyrienskies.eureka.forge

import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerPlayer
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.config.ModConfigEvent
import net.minecraftforge.registries.DeferredRegister
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.EurekaMod.init
import org.valkyrienskies.eureka.VsVersionWarning
import org.valkyrienskies.eureka.registry.CreativeTabs
import org.valkyrienskies.eureka.forge.registry.FuelRegistryImpl
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
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
        LOADING_CONTEXT.apply {
            registerConfig(ModConfig.Type.SERVER, EurekaConfig.EUREKA_SPEC, "valkyrienskies/vs_eureka.toml")
        }
        MOD_BUS.addListener(::onConfigReload)
        MOD_BUS.addListener(::onConfigLoad)
        FORGE_BUS.addListener(::onPlayerLoggedIn)
        FuelRegistryImpl()
        init()

        val deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EurekaMod.MOD_ID)
        deferredRegister.register("general") {
            CreativeTabs.create()
        }
        deferredRegister.register(getModBus())
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }

    private fun onConfigLoad(event: ModConfigEvent.Loading) {
        if (event.config.modId == EurekaMod.MOD_ID) {
            EurekaConfig.update(event.config)
        }
    }

    private fun onPlayerLoggedIn(event: PlayerEvent.PlayerLoggedInEvent) {
        val player = event.entity as? ServerPlayer ?: return
        val vsVersion = ModList.get().getModContainerById("valkyrienskies")
            .map { it.modInfo.version.toString() }
            .orElse(null)
        VsVersionWarning.onPlayerJoin(player, vsVersion)
    }

    private fun onConfigReload(event: ModConfigEvent.Reloading) {
        if (event.config.modId == EurekaMod.MOD_ID) {
            EurekaConfig.update(event.config)
        }
    }
}
