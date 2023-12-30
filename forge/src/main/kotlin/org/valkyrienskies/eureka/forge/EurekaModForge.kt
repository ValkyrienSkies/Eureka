package org.valkyrienskies.eureka.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import org.valkyrienskies.core.impl.config.VSConfigClass.Companion.getRegisteredConfig
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.EurekaMod.init
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
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
                    getRegisteredConfig(EurekaConfig::class.java)
                )
            }
        }
        init()
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
