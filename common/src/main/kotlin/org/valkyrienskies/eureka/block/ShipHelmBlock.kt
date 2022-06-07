package org.valkyrienskies.eureka.block

import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.ai.pathing.NavigationType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.sound.BlockSoundGroup
import net.minecraft.state.StateManager
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.world.BlockView
import net.minecraft.world.World
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes

val FACING = HorizontalFacingBlock.FACING!!

object ShipHelmBlock: BlockWithEntity(Settings.of(Material.WOOD).strength(2.5F).sounds(BlockSoundGroup.WOOD)) {
    val HELM_BASE = RotShapes.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    val HELM_POLE = RotShapes.box(4.0, 1.0, 7.0, 12.0, 12.0, 13.0)

    val HELM_SHAPE = DirectionalShape(RotShapes.union(HELM_BASE, HELM_POLE))

    override fun createBlockEntity(blockView: BlockView): BlockEntity = ShipHelmBlockEntity.supplier()

    override fun onUse(
        state: BlockState,
        world: World,
        pos: BlockPos,
        player: PlayerEntity,
        hand: Hand,
        blockHitResult: BlockHitResult
    ): ActionResult {
        if (!world.isClient) {
            if (player.isSneaking) {
                val factory = state.createScreenHandlerFactory(world, pos)
                if (factory != null) {
                    player.openHandledScreen(factory);
                }
                return ActionResult.SUCCESS;
            }
        }
        return super.onUse(state, world, pos, player, hand, blockHitResult)
    }

    init {
        defaultState = this.stateManager.defaultState.with(FACING, Direction.NORTH)
    }

    override fun getRenderType(arg: BlockState): BlockRenderType {
        return BlockRenderType.MODEL
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