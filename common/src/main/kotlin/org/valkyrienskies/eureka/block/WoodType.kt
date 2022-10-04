package org.valkyrienskies.eureka.block

import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.client.resources.model.Material
import net.minecraft.resources.ResourceLocation

// TODO mod compat
enum class WoodType(val textureName: String) {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    WARPED("warped"),
    CRIMSON("crimson");

    val textureLocationPlanks get() = ResourceLocation("minecraft:block/${textureName}_planks")
    val textureLocationLog get() = ResourceLocation("minecraft:block/${textureName}_log")
    val planksMaterial = Material(TextureAtlas.LOCATION_BLOCKS, textureLocationPlanks)
    val logMaterial = Material(TextureAtlas.LOCATION_BLOCKS, textureLocationLog)
}