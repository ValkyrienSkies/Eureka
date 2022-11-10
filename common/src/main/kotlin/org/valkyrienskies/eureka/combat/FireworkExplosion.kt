package org.valkyrienskies.eureka.combat

import com.google.common.collect.Sets
import com.mojang.datafixers.util.Pair
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.ExplosionDamageCalculator
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseFireBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import net.minecraft.world.phys.HitResult
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.eureka.EurekaConfig.SERVER
import org.valkyrienskies.mod.common.config.VSGameConfig
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.GameTickForceApplier
import org.valkyrienskies.mod.common.util.toJOML
import java.util.function.Consumer
import kotlin.math.ceil
import kotlin.math.sqrt

class FireworkExplosion(val level: Level, val exploder: Entity?, val dmgSource: DamageSource?, val context: ExplosionDamageCalculator, var x: Double, var y: Double, var z: Double, val size: Float, val power: Double, val direction: Vec3?, var angle: Double, val causesFire: Boolean, val mode: BlockInteraction) : Explosion(level, exploder, dmgSource, context, x, y, z, size, causesFire, mode) {

    override fun explode() {
        var l: Int
        var k: Int
        val set = Sets.newHashSet<BlockPos>()
        for (j in 0..15) {
            k = 0
            while (k < 16) {
                l = 0
                while (l < 16) {
                    if ((j != 0) && (j != 15) && (k != 0) && (k != 15) && (l != 0) && (l != 15)) {
                        ++l
                        continue
                    }
                    var directionX = (j.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                    var directionY = (k.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                    var directionZ = (l.toFloat() / 15.0f * 2.0f - 1.0f).toDouble()
                    val directionLength = sqrt(directionX * directionX + directionY * directionY + directionZ * directionZ)
                    directionX /= directionLength
                    directionY /= directionLength
                    directionZ /= directionLength
                    val traceDirection = Vec3(directionX, directionY, directionZ)
                    if (direction != null) {
                        if (traceDirection.dot(direction) <= angle) {
                            ++l
                            continue
                        }
                    }
                    var startPosX = this.x
                    var startPosY = this.y
                    var startPosZ = this.z
                    var traceStrength = radius * (0.7f + this.level.random.nextFloat() * 0.6f)
                    while (traceStrength > 0.0f) {
                        var fluidState: FluidState?
                        val blockPos = BlockPos(startPosX, startPosY, startPosZ)
                        val blockState = this.level.getBlockState(blockPos)
                        val optional = context.getBlockExplosionResistance(this, this.level, blockPos, blockState, this.level.getFluidState(blockPos).also { fluidState = it })
                        if (optional.isPresent) {
                            traceStrength -= (((optional.get() + 0.3f) * 0.3f) / power).toFloat()
                        }
                        if (traceStrength > 0.0f && context.shouldBlockExplode(this, this.level, blockPos, blockState, traceStrength)) {
                            set.add(blockPos)
                        }
                        startPosX += directionX * 0.3
                        startPosY += directionY * 0.3
                        startPosZ += directionZ * 0.3
                        traceStrength -= 0.215f
                    }
                    ++l
                }
                ++k
            }
        }
        toBlow.addAll(set)
    }

    /**
     * Does the second part of the explosion (sound, particles, drop spawn)
     */
    fun finalizeExplosion() {
        val bl: Boolean = mode != BlockInteraction.NONE
        if (bl) {
            val objectArrayList: ObjectArrayList<Pair<ItemStack, BlockPos>> = ObjectArrayList<Pair<ItemStack, BlockPos>>()
            toBlow.shuffle(this.level.random)
            for (blockPos in toBlow) {
                val blockState = this.level.getBlockState(blockPos)
                val block = blockState.block
                if (blockState.isAir) continue
                val blockPos2 = blockPos.immutable()
                this.level.profiler.push("firework_explosion_blocks")
                if (block.dropFromExplosion(this) && this.level is ServerLevel) {
                    val blockEntity = if (block.isEntityBlock) this.level.getBlockEntity(blockPos) else null
                    val builder = LootContext.Builder(this.level).withRandom(this.level.random).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockPos)).withParameter(LootContextParams.TOOL, ItemStack.EMPTY).withOptionalParameter(LootContextParams.BLOCK_ENTITY, blockEntity).withOptionalParameter(LootContextParams.THIS_ENTITY, source)
                    if (mode == BlockInteraction.DESTROY) {
                        builder.withParameter(LootContextParams.EXPLOSION_RADIUS, java.lang.Float.valueOf(radius))
                    }
                    blockState.getDrops(builder).forEach(Consumer { itemStack: ItemStack? ->
                        if (itemStack != null) {
                            addBlockDrops(objectArrayList, itemStack, blockPos2)
                        }
                    })
                }
                this.level.setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3)
                block.wasExploded(this.level, blockPos, this)
                this.level.profiler.pop()
            }
            for (pair in objectArrayList) {
                Block.popResource(this.level, pair.second as BlockPos, pair.first as ItemStack)
            }
        }
        if (causesFire) {
            for (blockPos3 in toBlow) {
                if (this.level.random.nextInt(3) != 0 || !this.level.getBlockState(blockPos3).isAir || !this.level.getBlockState(blockPos3.below()).isSolidRender(this.level, blockPos3.below())) continue
                this.level.setBlockAndUpdate(blockPos3, BaseFireBlock.getState(this.level, blockPos3))
            }
        }
    }

    private fun addBlockDrops(dropPositionArray: ObjectArrayList<Pair<ItemStack, BlockPos>>, stack: ItemStack, pos: BlockPos) {
        val i = dropPositionArray.size
        for (j in 0 until i) {
            val pair = dropPositionArray[j]
            val itemStack = pair.first
            if (!ItemEntity.areMergable(itemStack, stack)) continue
            val itemStack2 = ItemEntity.merge(itemStack, stack, 16)
            dropPositionArray[j] = Pair.of(itemStack2, pair.second)
            if (!stack.isEmpty) continue
            return
        }
        dropPositionArray.add(Pair.of(stack, pos))
    }

    fun doExplodeForce(knockback: Double) {
        // Custom forces
        val originPos = Vector3d(this.x, this.y, this.z)
        val explodePos = BlockPos(originPos.x(), originPos.y(), originPos.z())
        val radius = ceil(radius.toDouble()).toInt()
        for (x in radius downTo -radius) {
            for (y in radius downTo -radius) {
                for (z in radius downTo -radius) {
                    val result = level.clip(
                            ClipContext(Vec3.atCenterOf(explodePos),
                                    Vec3.atCenterOf(explodePos.offset(x, y, z)),
                                    ClipContext.Block.COLLIDER,
                                    ClipContext.Fluid.NONE, null))
                    if (result.type == HitResult.Type.BLOCK) {
                        val blockPos = result.blockPos
                        val ship = this.level.getShipObjectManagingPos(blockPos) as ServerShip?
                        if (ship != null) {
                            val forceVector = Vec3.atCenterOf(explodePos).toJOML() //Start at center position
                            val distanceMult = 0.5.coerceAtLeast(1.0 - this.radius / forceVector.distance(Vec3.atCenterOf(blockPos).toJOML()))
                            val powerMult = 0.1.coerceAtLeast((this.radius / 4).toDouble()) //TNT blast radius = 4
                            forceVector.sub(Vec3.atCenterOf(blockPos).toJOML()) //Subtract hit block pos to get direction
                            forceVector.normalize()
                            forceVector.mul(-1 *
                                    VSGameConfig.SERVER.explosionBlastForce) //Multiply by blast force at center position. Negative because of how we got the direction.
                            forceVector.mul(distanceMult) //Multiply by distance falloff
                            forceVector.mul(powerMult) //Multiply by radius, roughly equivalent to power
                            forceVector.mul(knockback) //Multiply by radius, roughly equivalent to power
                            val forceApplier = ship.getAttachment(GameTickForceApplier::class.java)
                            val shipCoords = ship.shipTransform.shipPositionInShipCoordinates
                            if (forceVector.isFinite) {
                                forceApplier!!.applyInvariantForceToPos(forceVector,
                                        Vec3.atCenterOf(blockPos).toJOML().sub(shipCoords))
                            }
                        }
                    }
                }
            }
        }
    }
}