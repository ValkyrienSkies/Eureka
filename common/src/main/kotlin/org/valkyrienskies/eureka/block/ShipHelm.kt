package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material

object ShipHelm: Block(Properties.of(Material.WOOD)) {

    override fun onPlace(
        blockState: BlockState?,
        level: Level?,
        blockPos: BlockPos?,
        blockState2: BlockState?,
        bl: Boolean
    ) {
        super.onPlace(blockState, level, blockPos, blockState2, bl)
        println("Works!")
    }

}