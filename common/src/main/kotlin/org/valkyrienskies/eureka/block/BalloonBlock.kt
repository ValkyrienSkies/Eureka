package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.block.Blocks

class BalloonBlock(properties: Properties) : Block(properties) {

    override fun fallOn(level: Level, state: BlockState, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.causeFallDamage(f, 0.2f, DamageSource.FALL)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level.isClientSide) return
        val serverLevel = level as ServerLevel

        val ship = level.getShipObjectManagingPos(pos) ?: level.getShipManagingPos(pos)
        if (ship != null) {
            EurekaShipControl.getOrCreate(ship).balloons += 1
        }

        val invalidDimensionsConfig = EurekaConfig.SERVER.balloonDimensionBlacklist
        if ( invalidDimensionsConfig.isNotEmpty() ) {
            val invalidDimensionLocations = invalidDimensionsConfig.mapNotNull { dimensionString ->
                try {
                    ResourceLocation(dimensionString)
                } catch (e: Exception) {
                    null
                }
            }.toSet()
            val currentDimensionLocation = serverLevel.dimension().location()
            if ( invalidDimensionLocations.contains(currentDimensionLocation) ) {
                // Replace the balloon block with air (Similar to how water is replaced in the Nether)
                serverLevel.setBlock(pos, Blocks.AIR.defaultBlockState(), 3)
                // Particles
                serverLevel.sendParticles(
                    ParticleTypes.LARGE_SMOKE,
                    pos.x + 0.5, // Center of the block
                    pos.y + 0.0,
                    pos.z + 0.5,
                    15, // Number of particles per iteration
                    0.1, 0.1, 0.1, // Spread in x, y, z directions
                    0.01 // Speed multiplier
                )
                // Sounds
                serverLevel.playSound(
                    null, // No specific player (plays for all nearby players)
                    pos,
                    SoundEvents.LAVA_EXTINGUISH, // Hiss-like sound
                    SoundSource.BLOCKS,
                    1.0f, // Volume
                    1.0f  // Pitch
                )
            }
        }
    }

    override fun onRemove(state: BlockState, level: Level, pos: BlockPos, newState: BlockState, isMoving: Boolean) {
        super.onRemove(state, level, pos, newState, isMoving)

        if (level.isClientSide) return
        level as ServerLevel

        level.getShipManagingPos(pos)?.getAttachment<EurekaShipControl>()?.let {
            it.balloons -= 1
        }
    }

    override fun onProjectileHit(level: Level, state: BlockState, hit: BlockHitResult, projectile: Projectile) {
        if (level.isClientSide) return

        level.destroyBlock(hit.blockPos, false)
        Direction.entries.forEach {
            val neighbor = hit.blockPos.relative(it)
            if (level.getBlockState(neighbor).block == this &&
                level.random.nextFloat() < EurekaConfig.SERVER.popSideBalloonChance
            ) {
                level.destroyBlock(neighbor, false)
            }
        }
    }
    
}
