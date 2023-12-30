package org.valkyrienskies.eureka.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.client.event.ModelEvent.BakingCompleted
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import org.valkyrienskies.core.impl.config.VSConfigClass.Companion.getRegisteredConfig
import org.valkyrienskies.eureka.EurekaBlockEntities.SHIP_HELM
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.EurekaMod.init
import org.valkyrienskies.eureka.EurekaMod.initClient
import org.valkyrienskies.eureka.block.WoodType
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer
import org.valkyrienskies.eureka.blockentity.renderer.WheelModels.setModelGetter
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(EurekaMod.MOD_ID)
class EurekaModForge {
    private var happendClientSetup = false

    init {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS.addListener { event: BakingCompleted ->
            clientSetup(
                event
            )
        }
        MOD_BUS.addListener { event: ModelEvent.RegisterAdditional ->
            onModelRegistry(
                event
            )
        }
        MOD_BUS.addListener { event: RegisterRenderers ->
            entityRenderers(
                event
            )
        }
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

    private fun clientSetup(event: BakingCompleted) {
        if (happendClientSetup) {
            return
        }
        happendClientSetup = true
        initClient()
        setModelGetter { woodType: WoodType ->
            event.modelBakery.bakedTopLevelModels
                .getOrDefault(
                    ResourceLocation(
                        EurekaMod.MOD_ID,
                        "block/" + woodType.resourceName + "_ship_helm_wheel"
                    ),
                    Minecraft.getInstance().modelManager.missingModel
                )
        }
    }

    private fun entityRenderers(event: RegisterRenderers) {
        event.registerBlockEntityRenderer(SHIP_HELM.get()) { ctx: BlockEntityRendererProvider.Context ->
            ShipHelmBlockEntityRenderer(
                ctx
            )
        }
    }

    private fun onModelRegistry(event: ModelEvent.RegisterAdditional) {
        for (woodType in WoodType.values()) {
            event.register(
                ResourceLocation(
                    EurekaMod.MOD_ID, "block/" + woodType.resourceName + "_ship_helm_wheel"
                )
            )
        }
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}
