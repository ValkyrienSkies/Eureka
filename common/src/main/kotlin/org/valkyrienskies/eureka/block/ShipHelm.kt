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

object ShipHelm: HorizontalFacingBlock(Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)) {
    val HELM_BASE = RotShapes.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    val HELM_POLE = RotShapes.box(4.0, 1.0, 7.0, 12.0, 12.0, 13.0)

    val HELM_SHAPE = DirectionalShape(RotShapes.union(HELM_BASE, HELM_POLE))


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
        return HELM_SHAPE[blockState[FACING]]
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