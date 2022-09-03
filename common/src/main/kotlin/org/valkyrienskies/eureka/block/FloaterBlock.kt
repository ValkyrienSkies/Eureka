package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipObjectManagingPos

object FloaterBlock : Block(
    Properties.of(Material.WOOD)
        .sound(SoundType.WOOL).strength(1.0f, 2.0f)
) {
    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipObjectManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.floaters += 1
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipObjectManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.floaters -= 1
        }
    }
}
