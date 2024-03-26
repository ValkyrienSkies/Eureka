package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER
import net.minecraft.world.level.material.Material
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos

class FloaterBlock : Block(
    Properties.of(Material.WOOD)
        .sound(SoundType.WOOL).strength(1.0f, 2.0f)
) {
    init {
        registerDefaultState(defaultBlockState().setValue(POWER, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(POWER)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val floaterPower = 15 - state.getValue(POWER)

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        EurekaShipControl.getOrCreate(ship).floaters += floaterPower
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        val currentPower = state.getValue(POWER)

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.floaters += (currentPower - signal)
        }

        level.setBlock(pos, state.setValue(POWER, signal), 2)
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val floaterPower = 15 - state.getValue(POWER)

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.floaters -= floaterPower
        }
    }
}
