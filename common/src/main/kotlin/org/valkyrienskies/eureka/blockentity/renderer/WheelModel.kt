package org.valkyrienskies.eureka.blockentity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.RenderType

object WheelModel : Model(RenderType::entitySolid) {
    // 16x16 size of original texture
    val wheel = ModelPart(16, 16, 0, 0)
    val rotatedPart = ModelPart(16, 16, 0, 0)
    val flatPart = ModelPart(16, 16, 0, 0)

    init {
        flatPart.addBox(-9.0f, -10.5f, 4.15f, 10.0f, 1.0f, 1f)
        flatPart.addBox(-4.5f, -15.0f, 4.15f, 1.0f, 10.0f, 1f)
        flatPart.addBox(-7.0f, -5.0f, 4.0f, 6.0f, 1.0f, 1f)
        flatPart.addBox(-7.0f, -16.0f, 4.0f, 6.0f, 1.0f, 1f)
        flatPart.addBox(1.0f, -13.0f, 4.0f, 1.0f, 6.0f, 1f)
        flatPart.addBox(-10.0f, -13.0f, 4.0f, 1.0f, 6.0f, 1f)

        rotatedPart.addBox(-6.35f, -2.125f, -0.4f, 1.0f, 4.0f, 1f)
        rotatedPart.addBox(-2.125f, -6.35f, -0.4f, 4.0f, 1.0f, 1f)
        rotatedPart.addBox(5.4f, -2.125f, -0.4f, 1.0f, 4.0f, 1f)
        rotatedPart.addBox(-2.125f, 5.4f, -0.4f, 4.0f, 1.0f, 1f)
        rotatedPart.addBox(-6.0f, -0.5f, -0.25f, 12.0f, 1.0f, 1f)
        rotatedPart.addBox(-0.5f, -6.0f, -0.25f, 1.0f, 12.0f, 1f)

        flatPart.x = 4f
        flatPart.y = 10f
        flatPart.z = -4.4f

        rotatedPart.x = 0f
        rotatedPart.y = 0f
        rotatedPart.z = 0f
        rotatedPart.zRot = 0.7854f // 45 degrees

        wheel.addChild(flatPart)
        wheel.addChild(rotatedPart)
    }

    override fun renderToBuffer(
        poseStack: PoseStack,
        buffer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {


        wheel.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha)
    }
}