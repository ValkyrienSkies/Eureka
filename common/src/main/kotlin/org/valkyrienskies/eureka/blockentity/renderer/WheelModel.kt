package org.valkyrienskies.eureka.blockentity.renderer

import com.google.common.collect.ImmutableMap
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.StateHolder
import net.minecraft.world.level.block.state.properties.EnumProperty
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.block.WoodType
import java.util.function.Function

// OK so what dis does im making mc happy about states
// WheelModels has many states (wood type)
// WheelModel has 1 woodtype and represents 1 state
// In the mixin it gets queued and abused
object WheelModels {
    private val mc get() = Minecraft.getInstance()
    private val property = EnumProperty.create("wood", WoodType::class.java)

    private val models by lazy { property.possibleValues.associateWith { WheelModel(it) } }

    fun render(
        matrixStack: PoseStack,
        blockEntity: BlockEntity,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        val level = blockEntity.level ?: return
        val woodType = (blockEntity.blockState.block as ShipHelmBlock).woodType

        matrixStack.pushPose()
        // Model isn't centered calculated and need to use 0.625 on y and z 0.25
        matrixStack.translate(-0.5, -0.625, -0.25)

        mc.blockRenderer.modelRenderer.tesselateWithoutAO(
            level,
            models[woodType]!!.model,
            blockEntity.blockState,
            blockEntity.blockPos,
            matrixStack,
            buffer.getBuffer(RenderType.cutout()),
            true,
            level.random,
            42L, // Used in ModelBlockRenderer.class in renderModel, not sure what the right number is but this seems to work
            combinedOverlay
        )

        matrixStack.popPose()
    }

    fun setModelGetter(getter: Function<WoodType, BakedModel>) {
        models.values.forEach { it.getter = getter::apply }
    }

    class WheelModel(type: WoodType) :
        StateHolder<WheelModels, WheelModel>(WheelModels, ImmutableMap.of(property, type), null) {

        var getter: (WoodType) -> BakedModel = { throw IllegalStateException("Getter not set") }

        val model by lazy { getter(type) }
    }
}
