package org.valkyrienskies.eureka.util

import com.google.common.collect.Sets
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import org.joml.AxisAngle4d
import org.joml.Matrix4d
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.networking.simple.sendToClient
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.assembly.createNewShipWithBlocks
import org.valkyrienskies.mod.common.executeIf
import org.valkyrienskies.mod.common.isTickingChunk
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.playerWrapper
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.logger
import org.valkyrienskies.mod.util.relocateBlock
import org.valkyrienskies.mod.util.updateBlock
import kotlin.collections.set
import kotlin.math.*

object ShipAssembler {
    fun collectBlocks(level: ServerLevel, center: BlockPos, predicate: (BlockState) -> Boolean): ServerShip? {
        val blocks = DenseBlockPosSet()

        blocks.add(center.toJOML())
        val result = bfs(level, center, blocks, predicate)
        if (result) {
            return createNewShipWithBlocks(center, blocks, level)
        } else {
            return null
        }
    }

    private fun roundToNearestMultipleOf(number: Double, multiple: Double) = multiple * round(number / multiple)

    // modified from https://gamedev.stackexchange.com/questions/83601/from-3d-rotation-snap-to-nearest-90-directions
    private fun snapRotation(direction: AxisAngle4d): AxisAngle4d {
        val x = abs(direction.x)
        val y = abs(direction.y)
        val z = abs(direction.z)
        val angle = roundToNearestMultipleOf(direction.angle, PI / 2)

        return if (x > y && x > z) {
            direction.set(angle, direction.x.sign, 0.0, 0.0)
        } else if (y > x && y > z) {
            direction.set(angle, 0.0, direction.y.sign, 0.0)
        } else {
            direction.set(angle, 0.0, 0.0, direction.z.sign)
        }
    }

    private fun rotationFromAxisAngle(axis: AxisAngle4d): Rotation {
        if (axis.y.absoluteValue < 0.1) {
            // if the axis isn't Y, either we're tilted up/down (which should not happen often) or we haven't moved and it's
            // along the z axis with a magnitude of 0 for some reason. In these cases, we don't rotate.
            return Rotation.NONE
        }

        // normalize into counterclockwise rotation (i.e. positive y-axis, according to testing + right hand rule)
        if (axis.y.sign < 0.0) {
            axis.y = 1.0
            // the angle is always positive and < 2pi coming in
            axis.angle = 2.0 * PI - axis.angle
            axis.angle %= (2.0 * PI)
        }

        val eps = 0.001
        if (axis.angle < eps)
            return Rotation.NONE
        else if ((axis.angle - PI / 2.0).absoluteValue < eps)
            return Rotation.COUNTERCLOCKWISE_90
        else if ((axis.angle - PI).absoluteValue < eps)
            return Rotation.CLOCKWISE_180
        else if ((axis.angle - 3.0 * PI / 2.0).absoluteValue < eps)
            return Rotation.CLOCKWISE_90
        else {
            logger.warn("failed to convert $axis into a rotation")
            return Rotation.NONE
        }
    }

    fun unfillShip(level: ServerLevel, ship: ServerShip, shipCenter: BlockPos, center: BlockPos) {
        ship.isStatic = true

        val rotation: Rotation = ship.transform.shipToWorldRotation
            .let(::AxisAngle4d)
            .let(ShipAssembler::snapRotation)
            .let(::rotationFromAxisAngle)

        // ship's rotation rounded to nearest 90*
        val shipToWorld = ship.transform.run {
            Matrix4d()
                .translate(positionInWorld)
                .rotate(snapRotation(AxisAngle4d(shipToWorldRotation)))
                .scale(shipToWorldScaling)
                .translate(-positionInShip.x(), -positionInShip.y(), -positionInShip.z())
        }

        val alloc0 = Vector3d()

        val chunksToBeUpdated = mutableMapOf<ChunkPos, Pair<ChunkPos, ChunkPos>>()

        ship.activeChunksSet.forEach { chunkX, chunkZ ->
            chunksToBeUpdated[ChunkPos(chunkX, chunkZ)] =
                Pair(ChunkPos(chunkX, chunkZ), ChunkPos(chunkX, chunkZ))
        }

        val chunkPairs = chunksToBeUpdated.values.toList()
        val chunkPoses = chunkPairs.flatMap { it.toList() }
        val chunkPosesJOML = chunkPoses.map { it.toJOML() }

        // Send a list of all the chunks that we plan on updating to players, so that they
        // defer all updates until assembly is finished
        level.players().forEach { player ->
            PacketStopChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper)
        }

        val toUpdate = Sets.newHashSet<Triple<BlockPos, BlockPos, BlockState>>()

        ship.activeChunksSet.forEach { chunkX, chunkZ ->
            val chunk = level.getChunk(chunkX, chunkZ)
            for (sectionIndex in 0 until chunk.sections.size) {
                val section = chunk.sections[sectionIndex]

                if (section == null || section.hasOnlyAir()) continue

                val bottomY = sectionIndex shl 4

                for (x in 0..15) {
                    for (y in 0..15) {
                        for (z in 0..15) {
                            val state = section.getBlockState(x, y, z)
                            if (state.isAir) continue

                            val realX = (chunkX shl 4) + x
                            val realY = bottomY + y + level.minBuildHeight
                            val realZ = (chunkZ shl 4) + z

                            val inWorldPos = shipToWorld.transformPosition(alloc0.set(realX + 0.5, realY + 0.5, realZ + 0.5)).floor()

                            val inWorldBlockPos = BlockPos(inWorldPos.x.toInt(), inWorldPos.y.toInt(), inWorldPos.z.toInt())
                            val inShipPos = BlockPos(realX, realY, realZ)

                            toUpdate.add(Triple(inShipPos, inWorldBlockPos, state))
                            level.relocateBlock(inShipPos, inWorldBlockPos, false, null, rotation)
                        }
                    }
                }
            }
        }
        // We update the blocks after they're set to prevent blocks from breaking
        for (triple in toUpdate) {
            updateBlock(level, triple.first, triple.second, triple.third)
        }

        level.server.executeIf(
            // This condition will return true if all modified chunks have been both loaded AND
            // chunk update packets were sent to players
            { chunkPoses.all(level::isTickingChunk) }
        ) {
            // Once all the chunk updates are sent to players, we can tell them to restart chunk updates
            level.players().forEach { player ->
                PacketRestartChunkUpdates(chunkPosesJOML).sendToClient(player.playerWrapper)
            }
        }
    }

    private fun bfs(
        level: ServerLevel,
        start: BlockPos,
        blocks: DenseBlockPosSet,
        predicate: (BlockState) -> Boolean
    ): Boolean {

        val blacklist = DenseBlockPosSet()
        val stack = ObjectArrayList<BlockPos>()

        directions(start) { stack.push(it) }

        while (!stack.isEmpty) {
            val pos = stack.pop()

            if (predicate(level.getBlockState(pos))) {
                blocks.add(pos.x, pos.y, pos.z)
                directions(pos) {
                    if (!blacklist.contains(it.x, it.y, it.z)) {
                        blacklist.add(it.x, it.y, it.z)
                        stack.push(it)
                    }
                }
            }
            if ((EurekaConfig.SERVER.maxShipBlocks > 0) and (blocks.size > EurekaConfig.SERVER.maxShipBlocks)) {
                logger.info("Stopped ship assembly due too many blocks")
                return false
            }
        }
        if (EurekaConfig.SERVER.maxShipBlocks > 0) {
            logger.info("Assembled ship with ${blocks.size} blocks, out of ${EurekaConfig.SERVER.maxShipBlocks} allowed")
        }
        return true
    }

    private fun directions(center: BlockPos, lambda: (BlockPos) -> Unit) {
        if (!EurekaConfig.SERVER.diagonals) Direction.values().forEach { lambda(center.relative(it)) }
        for (x in -1..1) {
            for (y in -1..1) {
                for (z in -1..1) {
                    if (x != 0 || y != 0 || z != 0) {
                        lambda(center.offset(x, y, z))
                    }
                }
            }
        }
    }

    private val logger by logger()
}
