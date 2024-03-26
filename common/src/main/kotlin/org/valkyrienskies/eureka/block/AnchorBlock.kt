package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.DirectionalShape
import org.valkyrienskies.eureka.util.RotShapes
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos

class AnchorBlock :
    HorizontalDirectionalBlock(Properties.of(Material.METAL).strength(5.0f, 1200.0f).sound(SoundType.ANVIL)) {

    val ANCHOR_BOTTOM = RotShapes.box(2.0, 2.0, 14.0, 14.0, 4.0, 16.0)
    val ANCHOR_ROD = RotShapes.box(7.0, 2.0, 14.0, 9.0, 24.0, 16.0)

    val ANCHOR_SHAPE = DirectionalShape(RotShapes.or(ANCHOR_BOTTOM, ANCHOR_ROD))

    init {
        registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        builder.add(FACING).add(BlockStateProperties.POWERED)
    }

    override fun getStateForPlacement(ctx: BlockPlaceContext): BlockState? {
        return defaultBlockState()
            .setValue(FACING, ctx.horizontalDirection.opposite)
            .setValue(
                BlockStateProperties.POWERED,
                ctx.level.hasNeighborSignal(ctx.clickedPos)
            )
    }

    override fun getShape(
        blockState: BlockState,
        blockGetter: BlockGetter,
        blockPos: BlockPos,
        collisionContext: CollisionContext
    ): VoxelShape {
        return ANCHOR_SHAPE[blockState.getValue(FACING)]
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        if (level.isClientSide) return
        level as ServerLevel

        val bl = level.hasNeighborSignal(pos)
        val prevBl = state.getValue(BlockStateProperties.POWERED)
        if (bl != prevBl)
            level.setBlock(pos, state.setValue(BlockStateProperties.POWERED, bl), 11)

        super.neighborChanged(state, level, pos, block, fromPos, isMoving)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val bl = state.getValue(BlockStateProperties.POWERED)

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        val attachment = EurekaShipControl.getOrCreate(ship)

        attachment.anchors += 1
        attachment.anchorsActive += if (bl) 1 else 0
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val bl = state.getValue(BlockStateProperties.POWERED)

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.anchors -= 1
            it.anchorsActive -= if (bl) 1 else 0
        }
    }
}
