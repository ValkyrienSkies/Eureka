package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

private val FACING = HorizontalDirectionalBlock.FACING!!

object Anchor: Block(Properties.of(Material.METAL).strength(5.0f, 1200.0f).sound(SoundType.ANVIL)) {
    val ANCHOR_BOTTOM = Shapes.box(2.0/16, 2.0/16, 14.0/16, 14.0/16, 4.0/16, 16.0/16)
    val ANCHOR_ROD = Shapes.box(7.0/16, 2.0/16, 14.0/16, 9.0/16, 24.0/16, 16.0/16)

    val ANCHOR_SHAPE = Shapes.or(ANCHOR_BOTTOM, ANCHOR_ROD).optimize()

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getShape(
        blockState: BlockState?,
        blockGetter: BlockGetter?,
        blockPos: BlockPos?,
        collisionContext: CollisionContext?
    ): VoxelShape {
        return ANCHOR_SHAPE
    }
}