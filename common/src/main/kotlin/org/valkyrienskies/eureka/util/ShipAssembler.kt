package org.valkyrienskies.eureka.util

import com.google.common.collect.Sets
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.impl.game.ships.ShipObjectServer
import org.valkyrienskies.core.impl.networking.simple.sendToClient
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.assembly.createNewShipWithBlocks
import org.valkyrienskies.mod.common.executeIf
import org.valkyrienskies.mod.common.isTickingChunk
import org.valkyrienskies.mod.common.networking.PacketRestartChunkUpdates
import org.valkyrienskies.mod.common.networking.PacketStopChunkUpdates
import org.valkyrienskies.mod.common.playerWrapper
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.relocateBlock

object ShipAssembler {
    fun collectBlocks(level: ServerLevel, center: BlockPos, predicate: (BlockState) -> Boolean): ServerShip {
        val blocks = DenseBlockPosSet()

        blocks.add(center.toJOML())
        bfs(level, center, blocks, predicate)
        return createNewShipWithBlocks(center, blocks, level)
    }

    fun unfillShip(level: ServerLevel, ship: ServerShip, direction: Direction, shipCenter: BlockPos, center: BlockPos) {
        ship as ShipObjectServer
        ship.shipData.isStatic = true

        val shipToWorld = ship.shipToWorld
        val alloc0 = Vector3d()
        val alloc1 = BlockPos.MutableBlockPos()
        val alloc2 = BlockPos.MutableBlockPos()

        val chunksToBeUpdated = mutableMapOf<ChunkPos, Pair<ChunkPos, ChunkPos>>()

        ship.shipActiveChunksSet.iterateChunkPos { chunkX, chunkZ ->
            val chunkBlockPos = BlockPos(chunkX shl 4, 0, chunkZ shl 4)
            val worldChunkPos = shipToWorld.transformPosition(alloc0.set(chunkBlockPos.x.toDouble(), chunkBlockPos.y.toDouble(), chunkBlockPos.z.toDouble()))
            val worldChunkBlockPos = BlockPos(worldChunkPos.x, 0.0, worldChunkPos.z)

            chunksToBeUpdated[ChunkPos(chunkX, chunkZ)] = Pair(ChunkPos(chunkX, chunkZ), level.getChunkAt(worldChunkBlockPos).pos)
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

        ship.shipActiveChunksSet.iterateChunkPos { chunkX, chunkZ ->
            val chunk = level.getChunk(chunkX, chunkZ)
            for (section in chunk.sections) {
                if (section == null) continue
                for (x in 0..15) {
                    for (y in 0..15) {
                        for (z in 0..15) {
                            val state = section.getBlockState(x, y, z)
                            if (state.isAir) continue

                            val realX = (chunkX shl 4) + x
                            val realY = section.bottomBlockY() + y
                            val realZ = (chunkZ shl 4) + z

                            val inWorldPos = shipToWorld.transformPosition(
                                alloc0.set(realX.toDouble() + 0.5, realY.toDouble() + 0.5, realZ.toDouble() + 0.5)
                            ).round()

                            val inWorldBlockPos =
                                alloc1.set(inWorldPos.x.toInt(), inWorldPos.y.toInt(), inWorldPos.z.toInt())
                            val inShipPos =
                                alloc2.set(realX, realY, realZ)

                            toUpdate.add(Triple<BlockPos, BlockPos, BlockState>(inShipPos.immutable(), inWorldBlockPos.immutable(), state))
                            level.relocateBlock(inShipPos, inWorldBlockPos, null, direction)
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
    ) {

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
        }
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

    /**
     * Update block after relocate
     *
     * @param level
     * @param fromPos old position coordinate
     * @param toPos new position coordinate
     * @param toState new blockstate at toPos
     */
    @Deprecated("We should move this to VS2")
    private fun updateBlock(level: Level, fromPos: BlockPos, toPos: BlockPos, toState: BlockState) {
        val AIR = Blocks.AIR.defaultBlockState()

        // 75 = flag 1 (block update) & flag 2 (send to clients) + flag 8 (force rerenders)
        val flags = 11

        // updateNeighbourShapes recurses through nearby blocks, recursionLeft is the limit
        val recursionLeft = 511

        level.setBlocksDirty(fromPos, toState, AIR)
        level.sendBlockUpdated(fromPos, toState, AIR, flags)
        level.blockUpdated(fromPos, AIR.block)
        // This handles the update for neighboring blocks in worldspace
        AIR.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft - 1)
        AIR.updateNeighbourShapes(level, fromPos, flags, recursionLeft)
        AIR.updateIndirectNeighbourShapes(level, fromPos, flags, recursionLeft)
        // This updates lighting for blocks in worldspace
        level.chunkSource.lightEngine.checkBlock(fromPos)

        level.setBlocksDirty(toPos, AIR, toState)
        level.sendBlockUpdated(toPos, AIR, toState, flags)
        level.blockUpdated(toPos, toState.block)
        if (!level.isClientSide && toState.hasAnalogOutputSignal()) {
            level.updateNeighbourForOutputSignal(toPos, toState.block)
        }
        // This updates lighting for blocks in shipspace
        level.chunkSource.lightEngine.checkBlock(toPos)
    }
}
