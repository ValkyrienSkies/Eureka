package org.valkyrienskies.eureka.util

import it.unimi.dsi.fastutil.Stack
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3i
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.core.hooks.VSEvents.ShipLoadEvent
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.util.relocateBlock
import org.valkyrienskies.mod.common.util.toBlockPos

object ShipAssembler {
    // TODO use dense packed to send updates
    // with a more optimized algorithm for bigger ships
    private val MAX_SIZE = 32 * 16

    fun fillShip(level: ServerLevel, ship: ServerShip, center: BlockPos, predicate: (BlockState) -> Boolean) {
        val shipCenter = (ship as ShipData).chunkClaim.getCenterBlockCoordinates(Vector3i()).toBlockPos()
        ShipLoadEvent.on { evt, _ -> println("Ship loaded: ${evt.ship.shipData.id}") }

        // wait until this ship is loaded to copy blocks
        ShipLoadEvent.once({ it.ship.shipData == ship }) {
            val todo = ObjectArrayList<Pair<BlockPos, BlockPos>>()
            val changed = mutableSetOf<BlockPos>()

            move(level, ship, shipCenter, center, changed)
            directions(shipCenter, center) { a, b -> todo.push(Pair(a, b)) }

            while (!todo.isEmpty) {
                val (to, from) = todo.pop()

                if (from.distSqr(center) > (MAX_SIZE * MAX_SIZE)) continue
                bfs(level, ship, to, from, todo, predicate, changed)
            }

            changed.forEach {
                level.updateNeighborsAt(it, level.getBlockState(it).block)
            }
        }
    }

    private fun bfs(
        level: ServerLevel,
        ship: ServerShip,
        new: BlockPos,
        old: BlockPos,
        stack: Stack<Pair<BlockPos, BlockPos>>,
        predicate: (BlockState) -> Boolean,
        modifications: MutableSet<BlockPos>
    ) {

        if (predicate(level.getBlockState(old))) {
            move(level, ship, new, old, modifications)

            directions(new, old) { a, b -> stack.push(Pair(a, b)) }
        }
    }

    private fun directions(new: BlockPos, old: BlockPos, lambda: (BlockPos, BlockPos) -> Unit) {
        if (!EurekaConfig.SERVER.diagonals) Direction.values().forEach { lambda(new.relative(it), old.relative(it)) }

        fun minusOneAndOne(lambda: (Int) -> Unit) {
            lambda(-1)
            lambda(1)
        }

        minusOneAndOne { x ->
            minusOneAndOne { Y ->
                minusOneAndOne { z ->
                    lambda(
                        new.offset(x, Y, z),
                        old.offset(x, Y, z)
                    )
                }
            }
        }
    }

    private fun move(
        level: ServerLevel,
        ship: ServerShip,
        new: BlockPos,
        old: BlockPos,
        modifications: MutableSet<BlockPos>
    ) {
        modifications += new
        modifications += old

        level.relocateBlock(old, new, ship)
    }
}
