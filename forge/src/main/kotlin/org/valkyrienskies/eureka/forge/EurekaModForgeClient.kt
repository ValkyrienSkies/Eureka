package org.valkyrienskies.eureka.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.client.event.EntityRenderersEvent
import net.minecraftforge.client.event.ModelEvent
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.block.WoodType
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer
import org.valkyrienskies.eureka.blockentity.renderer.WheelModels
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object EurekaModForgeClient {
    private var happendClientSetup = false

    fun registerClient() {
        MOD_BUS.addListener { event: ModelEvent.BakingCompleted ->
            clientSetup(
                event
            )
        }
        MOD_BUS.addListener { event: ModelEvent.RegisterAdditional ->
            onModelRegistry(
                event
            )
        }
        MOD_BUS.addListener { event: EntityRenderersEvent.RegisterRenderers ->
            entityRenderers(
                event
            )
        }
    }

    fun clientSetup(event: ModelEvent.BakingCompleted) {
        if (happendClientSetup) {
            return
        }
        happendClientSetup = true
        EurekaMod.initClient()
        WheelModels.setModelGetter { woodType: WoodType ->
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

    fun entityRenderers(event: EntityRenderersEvent.RegisterRenderers) {
        event.registerBlockEntityRenderer(EurekaBlockEntities.SHIP_HELM.get()) { ctx: BlockEntityRendererProvider.Context ->
            ShipHelmBlockEntityRenderer(
                ctx
            )
        }
    }

    fun onModelRegistry(event: ModelEvent.RegisterAdditional) {
        for (woodType in WoodType.values()) {
            event.register(
                ResourceLocation(
                    EurekaMod.MOD_ID, "block/" + woodType.resourceName + "_ship_helm_wheel"
                )
            )
        }
    }
}
