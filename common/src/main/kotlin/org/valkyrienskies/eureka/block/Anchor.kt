package org.valkyrienskies.eureka.block

import net.minecraft.block.*
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes

object Anchor: HorizontalFacingBlock(Settings.of(Material.METAL).strength(5.0f, 1200.0f).sounds(BlockSoundGroup.ANVIL)) {
    val ANCHOR_BOTTOM = RotShapes.box(2.0, 2.0, 14.0, 14.0, 4.0, 16.0)
    val ANCHOR_ROD = RotShapes.box(7.0, 2.0, 14.0, 9.0, 24.0, 16.0)

    val ANCHOR_SHAPE = DirectionalShape(RotShapes.union(ANCHOR_BOTTOM, ANCHOR_ROD))

    init {
        defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH)
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState {
        return defaultState.with(FACING, ctx.playerFacing.opposite)
    }

    override fun getOutlineShape(
        blockState: BlockState,
        blockView: BlockView,
        blockPos: BlockPos,
        shapeContext: ShapeContext
    ): VoxelShape {
        return ANCHOR_SHAPE[blockState[FACING]]
    }

    override fun canPathfindThrough(
        blockState: BlockState,
        blockView: BlockView,
        blockPos: BlockPos,
        navigationType: NavigationType
    ): Boolean {
        return false
    }
}