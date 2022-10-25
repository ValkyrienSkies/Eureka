package org.valkyrienskies.eureka.util

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3d
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.game.ships.ShipObjectServer
import org.valkyrienskies.core.util.logger
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.assembly.createNewShipWithBlocks
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.util.relocateBlock

object ShipAssembler {
    private val MAX_SIZE = 32 * 16

    fun collectBlocks(level: ServerLevel, center: BlockPos, predicate: (BlockState) -> Boolean): ServerShip {
        val blocks = DenseBlockPosSet()

        blocks.add(center.toJOML())
        bfs(level, center, blocks, predicate)
        return createNewShipWithBlocks(center, blocks, level)
    }

    fun unfillShip(level: ServerLevel, ship: ServerShip, direction: Direction, shipCenter: BlockPos, center: BlockPos) {
        ship as ShipObjectServer // TODO fix this
        ship.shipData.isStatic = true

        val shipToWorld = ship.shipToWorld
        val alloc0 = Vector3d()
        val alloc1 = BlockPos.MutableBlockPos()
        val alloc2 = BlockPos.MutableBlockPos()
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
                                alloc0.set(realX.toDouble(), realY.toDouble(), realZ.toDouble())
                            ).round()

                            val inWorldBlockPos =
                                alloc1.set(inWorldPos.x.toInt(), inWorldPos.y.toInt(), inWorldPos.z.toInt())
                            val inShipPos =
                                alloc2.set(realX, realY, realZ)

                            level.relocateBlock(inShipPos, inWorldBlockPos, null, direction)
                        }
                    }
                }
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

    private val logger by logger()
}
