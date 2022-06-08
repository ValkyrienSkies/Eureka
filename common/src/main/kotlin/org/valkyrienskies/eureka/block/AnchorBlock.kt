package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes

object AnchorBlock :
    HorizontalDirectionalBlock(Properties.of(Material.METAL).strength(5.0f, 1200.0f).sound(SoundType.ANVIL)) {

    val ANCHOR_BOTTOM = RotShapes.box(2.0, 2.0, 14.0, 14.0, 4.0, 16.0)
    val ANCHOR_ROD = RotShapes.box(7.0, 2.0, 14.0, 9.0, 24.0, 16.0)

    val ANCHOR_SHAPE = DirectionalShape(RotShapes.or(ANCHOR_BOTTOM, ANCHOR_ROD))

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return ANCHOR_SHAPE[blockState.getValue(FACING)]
    }
}
