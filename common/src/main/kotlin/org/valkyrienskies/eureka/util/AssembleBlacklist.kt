package org.valkyrienskies.eureka.util

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation

val BLOCK_BLACKLIST = hashSetOf(
    "vs_eureka:ship_helm",
    "minecraft:dirt",
    "minecraft:grass_block",
    "minecraft:stone",
    "minecraft:bedrock",
    "minecraft:sand",
    "minecraft:gravel",
    "minecraft:air"
)

val BLOCK_BLACKLIST_B = BLOCK_BLACKLIST.map { Registry.BLOCK[ResourceLocation.tryParse(it)] }
