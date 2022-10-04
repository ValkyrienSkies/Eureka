package org.valkyrienskies.eureka.blockentity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

class ShipHelmBlockEntityRenderer(blockEntityRenderDispatcher: BlockEntityRenderDispatcher) :
    BlockEntityRenderer<ShipHelmBlockEntity>(blockEntityRenderDispatcher) {

    val WHEEL_TEXTURE = Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation("minecraft:block/oak_planks"))

    override fun render(
        blockEntity: ShipHelmBlockEntity,
        partialTicks: Float,
        matrixStack: PoseStack,
        buffer: MultiBufferSource,
        combinedLight: Int,
        combinedOverlay: Int
    ) {
        matrixStack.pushPose()
        // Wheel offset of the base
        matrixStack.translate(0.5, 0.65, 0.5)
        // Rotate wheel towards the direction its facing
        matrixStack.mulPose(
            Vector3f.YP.rotationDegrees(
                -blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
            )
        )
        // Add offset of the base based of rotation
        matrixStack.translate(0.0, 0.0, 0.2)
        // Rotate the wheel based of the ship omega
        matrixStack.mulPose(Vector3f.ZP.rotation(((blockEntity.level!!.gameTime % 40) + partialTicks) / 20f * Math.PI.toFloat()))
        // Render the wheel
        val vertexConsumer = WHEEL_TEXTURE.buffer(buffer, RenderType::entitySolid)
        WheelModel.renderToBuffer(matrixStack, vertexConsumer, combinedLight, combinedOverlay, 1.0f, 1.0f, 1.0f, 1.0f)

        matrixStack.popPose()
    }
}