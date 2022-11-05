package org.valkyrienskies.eureka.blockentity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.mod.common.getShipManagingPos

class ShipHelmBlockEntityRenderer(val ctx: BlockEntityRendererProvider.Context) :
    BlockEntityRenderer<ShipHelmBlockEntity> {

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
        matrixStack.translate(0.5, 0.60, 0.5)
        // Rotate wheel towards the direction its facing
        matrixStack.mulPose(
            Vector3f.YP.rotationDegrees(
                -blockEntity.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).toYRot()
            )
        )
        val ship = (blockEntity.level)?.getShipManagingPos(blockEntity.blockPos)
        var rot = 0.0
        if (ship != null) {
            rot = ship.omega.y()
        }
        // Add offset of the base based of rotation
        matrixStack.translate(0.0, 0.0, 0.19)
        // Rotate the wheel based of the ship omega
        matrixStack.mulPose(Vector3f.ZP.rotation((rot / 20f * Math.PI.toFloat()).toFloat()))
        // Render the wheel
        WheelModels.render(matrixStack, blockEntity, buffer, combinedLight, combinedOverlay)

        matrixStack.popPose()
    }
}
