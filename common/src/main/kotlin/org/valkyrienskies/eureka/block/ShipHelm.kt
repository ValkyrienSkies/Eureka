package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

private val FACING = HorizontalDirectionalBlock.FACING!!

object ShipHelm: Block(Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)) {
    val HELM_BASE = Shapes.box(1.0/16, 0.0/16, 1.0/16, 15.0/16, 1.0/16, 15.0/16)
    val HELM_POLE = Shapes.box(4.0/16, 1.0/16, 7.0/16, 12.0/16, 12.0/16, 13.0/16)

    val HELM_SHAPE = Shapes.or(HELM_BASE, HELM_POLE).optimize()


    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun onPlace(
        blockState: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState2: BlockState,
        bl: Boolean
    ) {
        super.onPlace(blockState, level, blockPos, blockState2, bl)
        println("Works!")
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING)
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return HELM_SHAPE
    }

    override fun useShapeForLightOcclusion(blockState: BlockState): Boolean {
        return true
    }

    override fun isPathfindable(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        pathComputationType: PathComputationType
    ): Boolean {
        return false;
    }

}