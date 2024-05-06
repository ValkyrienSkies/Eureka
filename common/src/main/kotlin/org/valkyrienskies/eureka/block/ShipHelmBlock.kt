package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.pathfinder.PathComputationType
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import java.awt.TextComponent

class ShipHelmBlock(properties: Properties, val woodType: WoodType) : BaseEntityBlock(properties) {
    val HELM_BASE = RotShapes.box(2.0, 0.0, 2.0, 14.0, 2.0, 14.0)
    val HELM_POLE = RotShapes.box(4.0, 2.0, 5.0, 12.0, 13.0, 13.0)

    val HELM_SHAPE = DirectionalShape(RotShapes.or(HELM_BASE, HELM_POLE))

    init {
        registerDefaultState(this.stateDefinition.any().setValue(HORIZONTAL_FACING, Direction.NORTH))
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        EurekaShipControl.getOrCreate(ship).helms += 1
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let { control ->

            if (control.helms <= 1 && control.seatedPlayer?.vehicle?.type == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE) {
                control.seatedPlayer!!.unRide()
                control.seatedPlayer = null
            }

            control.helms -= 1
        }
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

        return if (player.isSecondaryUseActive) {
            player.openMenu(blockEntity)
            InteractionResult.CONSUME
        } else if (level.getShipManagingPos(pos) == null) {
            player.displayClientMessage(Component.literal("Sneak to open the ship helm!"), true)
            InteractionResult.CONSUME
        } else if (blockEntity.sit(player)) {
            InteractionResult.CONSUME
        } else InteractionResult.PASS
    }

    override fun getRenderShape(blockState: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(HORIZONTAL_FACING, ctx.horizontalDirection.opposite)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(HORIZONTAL_FACING)
    }

    override fun newBlockEntity(blockPos: BlockPos, state: BlockState): BlockEntity {
        return ShipHelmBlockEntity(blockPos, state)
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return HELM_SHAPE[blockState.getValue(HORIZONTAL_FACING)]
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

    override fun rotate(state: BlockState, rotation: Rotation): BlockState? {
        return state.setValue(HORIZONTAL_FACING, rotation.rotate(state.getValue(HORIZONTAL_FACING) as Direction)) as BlockState
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        type: BlockEntityType<T>
    ): BlockEntityTicker<T> = BlockEntityTicker { level, pos, state, blockEntity ->
        if (level.isClientSide) return@BlockEntityTicker
        if (blockEntity is ShipHelmBlockEntity) {
            blockEntity.tick()
        }
    }
}
