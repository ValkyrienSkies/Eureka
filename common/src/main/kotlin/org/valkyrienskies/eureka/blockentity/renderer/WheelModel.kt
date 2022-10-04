package org.valkyrienskies.eureka.blockentity.renderer

import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.StateHolder
import net.minecraft.world.level.block.state.properties.EnumProperty
import org.valkyrienskies.eureka.EurekaMod
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.block.WoodType

// OK so what dis does im making mc happy about states
// WheelModels has many states (wood type)
// WheelModel has 1 woodtype and represents 1 state
// In the mixin it gets queued and abused
object WheelModels {
    val mc by lazy { Minecraft.getInstance() }
    val property = EnumProperty.create("wood", WoodType::class.java)

    val models by lazy { property.possibleValues.associateWith { WheelModel(it) } }
    val definition = object : StateDefinition<WheelModels, WheelModel>(
        { models.values.first() }, WheelModels,
        { a, b, c -> WheelModel(b[property!!] as WoodType) },
        mapOf(Pair("wood", property))
    ) {}


    fun render(
        matrixStack: PoseStack,
        blockEntity: BlockEntity,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        val woodType = (blockEntity.blockState.block as ShipHelmBlock).woodType

        matrixStack.pushPose()
        // Model isn't centered calculated and need to use 0.625 on y and z 0.25
        matrixStack.translate(-0.5, -0.625, -0.25)

        mc.blockRenderer.modelRenderer.renderModel(
            matrixStack.last(),
            buffer.getBuffer(RenderType.cutout()),
            null,
            models[woodType]!!.model,
            1f, 1f, 1f,
            combinedLight,
            combinedOverlay
        )

        matrixStack.popPose()
    }

    class WheelModel(type: WoodType) :
        StateHolder<WheelModels, WheelModel>(WheelModels, ImmutableMap.of(property, type), null) {

        val location = ModelResourceLocation(
            ResourceLocation(EurekaMod.MOD_ID, "ship_helm_wheel"),
            "wood=${type.resourceName}"
        )

        val model by lazy {
            mc.blockRenderer.blockModelShaper.modelManager.getModel(location)
        }
    }
}