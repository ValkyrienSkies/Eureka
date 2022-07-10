package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes

private val FACING = HorizontalDirectionalBlock.FACING!!

object ShipHelmBlock : BaseEntityBlock(Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)) {
    val HELM_BASE = RotShapes.box(1.0, 0.0, 1.0, 15.0, 1.0, 15.0)
    val HELM_POLE = RotShapes.box(4.0, 1.0, 7.0, 12.0, 12.0, 13.0)

    val HELM_SHAPE = DirectionalShape(RotShapes.or(HELM_BASE, HELM_POLE))

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult
    ): InteractionResult {
        if (level.isClientSide) return InteractionResult.SUCCESS
        val blockEntity = level.getBlockEntity(pos) as ShipHelmBlockEntity

        return if (player.isCrouching) {
            player.openMenu(blockEntity)
            InteractionResult.CONSUME
        } else if (player.startRiding(blockEntity.seat)) {
            InteractionResult.CONSUME
        } else InteractionResult.PASS
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

    override fun newBlockEntity(blockGetter: BlockGetter): BlockEntity {
        return ShipHelmBlockEntity()
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return

        val be = level.getBlockEntity(pos) as ShipHelmBlockEntity
        try {
            be.initSeat(pos, state, level as ServerLevel)
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        if (level.isClientSide) return

        val be = level.getBlockEntity(pos) as ShipHelmBlockEntity
        be.seat?.remove()
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return HELM_SHAPE[blockState.getValue(FACING)]
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
        return false
    }
}
