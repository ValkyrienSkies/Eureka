package org.valkyrienskies.eureka.block

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks

public enum class WoodType(final val logBlock: Block, final val plankBlock: Block) : StringRepresentable, IWoodType {
    OAK(Blocks.OAK_LOG, Blocks.OAK_PLANKS),
    SPRUCE(Blocks.SPRUCE_LOG, Blocks.SPRUCE_PLANKS),
    BIRCH(Blocks.BIRCH_LOG, Blocks.BIRCH_PLANKS),
    JUNGLE(Blocks.JUNGLE_LOG, Blocks.JUNGLE_PLANKS),
    ACACIA(Blocks.ACACIA_LOG, Blocks.ACACIA_PLANKS),
    DARK_OAK(Blocks.DARK_OAK_LOG, Blocks.DARK_OAK_PLANKS),
    CRIMSON(Blocks.CRIMSON_STEM, Blocks.CRIMSON_PLANKS),
    WARPED(Blocks.WARPED_STEM, Blocks.WARPED_PLANKS);

    override fun getSerializedName(): String = name.lowercase()

    override fun getWood(): Block = logBlock

    override fun getPlanks(): Block = plankBlock
}
