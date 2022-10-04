package org.valkyrienskies.eureka.blockentity.renderer

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.eureka.block.WoodType

object WheelModel {
    // 16x16 size of original texture
    val flatSpokes = ModelPart(16, 16, 0, 0)
    val rotatedSpokes = ModelPart(16, 16, 0, 0)
    val rotatedRim = ModelPart(16, 16, 0, 0)
    val flatRim = ModelPart(16, 16, 0, 0)
    val middle = ModelPart(16, 16, 0, 0)

    val goldMaterial = Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation("minecraft:block/gold_block"))

    init {
        flatRim.mirror = true
        flatSpokes.mirror = true
        rotatedRim.mirror = true
        rotatedSpokes.mirror = true

        middle.addBox(-1.0f, -1.0f, -1.0f, 2.0f, 2.0f, 2.0f)

        flatSpokes.addBox(-9.0f, -10.5f, 4.15f, 10.0f, 1.0f, 1f)
        flatSpokes.addBox(-4.5f, -15.0f, 4.15f, 1.0f, 10.0f, 1f)
        flatRim.addBox(-7.0f, -5.0f, 4.0f, 6.0f, 1.0f, 1f)
        flatRim.addBox(-7.0f, -16.0f, 4.0f, 6.0f, 1.0f, 1f)
        flatRim.addBox(1.0f, -13.0f, 4.0f, 1.0f, 6.0f, 1f)
        flatRim.addBox(-10.0f, -13.0f, 4.0f, 1.0f, 6.0f, 1f)

        rotatedSpokes.addBox(-6.0f, -0.5f, -0.25f, 12.0f, 1.0f, 1f)
        rotatedSpokes.addBox(-0.5f, -6.0f, -0.25f, 1.0f, 12.0f, 1f)
        rotatedRim.addBox(-6.35f, -2.125f, -0.4f, 1.0f, 4.0f, 1f)
        rotatedRim.addBox(-2.125f, -6.35f, -0.4f, 4.0f, 1.0f, 1f)
        rotatedRim.addBox(5.4f, -2.125f, -0.4f, 1.0f, 4.0f, 1f)
        rotatedRim.addBox(-2.125f, 5.4f, -0.4f, 4.0f, 1.0f, 1f)

        flatSpokes.x = 4f
        flatSpokes.y = 10f
        flatSpokes.z = -4.4f

        flatRim.x = 4f
        flatRim.y = 10f
        flatRim.z = -4.4f

        rotatedSpokes.zRot = 0.7854f // 45 degrees
        rotatedRim.zRot = 0.7854f
    }

    fun renderToBuffer(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        woodType: WoodType
    ) {
        woodType.planksMaterial.buffer(buffer, RenderType::entitySolid).let {
            flatSpokes.render(poseStack, it, packedLight, packedOverlay, 1f, 1f, 1f, 1f)
            rotatedSpokes.render(poseStack, it, packedLight, packedOverlay, 1f, 1f, 1f, 1f)
        }

        woodType.logMaterial.buffer(buffer, RenderType::entitySolid).let {
            flatRim.render(poseStack, it, packedLight, packedOverlay, 1f, 1f, 1f, 1f)
            rotatedRim.render(poseStack, it, packedLight, packedOverlay, 1f, 1f, 1f, 1f)
        }

        goldMaterial.buffer(buffer, RenderType::entitySolid).let {
            middle.render(poseStack, it, packedLight, packedOverlay, 1f, 1f, 1f, 1f)
        }
    }
}