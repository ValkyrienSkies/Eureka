package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos

// EnderAnchor
class EnderAnchorBlock : Block(
    Properties.of(Material.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos) ?: return
        EurekaShipControl.getOrCreate(ship).balloons += 1
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.balloons -= 1
        }
    }

    // TODO: Make this break on hit from an arrow?
    /*
    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (level.isClientSide) return

        level.destroyBlock(hit.blockPos, false)
        Direction.values().forEach {
            val neighbor = hit.blockPos.relative(it)
            if (level.getBlockState(neighbor).block == this &&
                level.random.nextFloat() < EurekaConfig.SERVER.popSideBalloonChance
            ) {
                level.destroyBlock(neighbor, false)
            }
        }
    }
     */
}
