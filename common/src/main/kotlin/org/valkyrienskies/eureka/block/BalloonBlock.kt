package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipObjectManagingPos

object BalloonBlock : Block(
    Properties.of(Material.WOOL, MaterialColor.WOOL).sound(SoundType.WOOL)
) {

    override fun fallOn(level: Level, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.causeFallDamage(f, 0.2f)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipObjectManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.balloons += 1
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipObjectManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.balloons -= 1
        }
    }

    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (level.isClientSide) return

        level.destroyBlock(hit.blockPos, false)
        Direction.values().forEach {
            if (level.random.nextFloat() < EurekaConfig.SERVER.popSideBalloonChance) {
                level.destroyBlock(hit.blockPos.relative(it), false)
            }
        }
    }
}
