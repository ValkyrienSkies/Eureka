package org.valkyrienskies.eureka.block

import net.minecraft.resources.ResourceLocation
import net.minecraft.util.StringRepresentable

// TODO mod compat

enum class WoodType(val resourceName: String) : StringRepresentable {
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    ACACIA("acacia"),
    DARK_OAK("dark_oak"),
    WARPED("warped"),
    CRIMSON("crimson");

    val textureLocationPlanks get() = ResourceLocation("minecraft:block/${resourceName}_planks")
    val textureLocationLog get() = ResourceLocation("minecraft:block/${resourceName}_log")

    override fun getSerializedName(): String = resourceName
}
