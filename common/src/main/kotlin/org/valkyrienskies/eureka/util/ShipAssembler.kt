package org.valkyrienskies.eureka.util

import it.unimi.dsi.fastutil.Stack
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.joml.Vector3i
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.core.hooks.VSEvents.ShipLoadEvent
import org.valkyrienskies.core.util.logger
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.util.relocateBlock
import org.valkyrienskies.mod.common.util.toBlockPos

object ShipAssembler {
    // TODO use dense packed to send updates
    // with a more optimized algorithm for bigger ships
    private val MAX_SIZE = 32 * 16
    private val tasks = mutableListOf<AssemblyTask>()

    fun fillShip(level: ServerLevel, ship: ServerShip, center: BlockPos, predicate: (BlockState) -> Boolean) {
        val shipCenter = (ship as ShipData).chunkClaim.getCenterBlockCoordinates(Vector3i()).toBlockPos()
        ShipLoadEvent.on { evt, _ -> println("Ship loaded: ${evt.ship.shipData.id}") }

        ship.isStatic = true

        // wait until this ship is loaded to copy blocks
        ShipLoadEvent.once({ it.ship.shipData == ship }) {
            val task = AssemblyTask(level, ship, center, predicate) {
                ship.isStatic = false
                logger.info("Ship assembled!")
            }

            move(level, ship, shipCenter, center) {}
            directions(shipCenter, center) { a, b -> task.todo.push(Pair(a, b)) }

            tasks += task
        }
    }

    private fun bfs(
        level: ServerLevel,
        ship: ServerShip,
        new: BlockPos,
        old: BlockPos,
        stack: Stack<Pair<BlockPos, BlockPos>>,
        predicate: (BlockState) -> Boolean,
        modifications: MutableSet<Pair<Level, BlockPos>>
    ) {

        if (predicate(level.getBlockState(old))) {
            move(level, ship, new, old, modifications::add)

            directions(new, old) { a, b -> stack.push(Pair(a, b)) }
        }
    }

    private fun directions(new: BlockPos, old: BlockPos, lambda: (BlockPos, BlockPos) -> Unit) {
        if (!EurekaConfig.SERVER.diagonals) Direction.values().forEach { lambda(new.relative(it), old.relative(it)) }

        fun minusOneOneZero(lambda: (Int) -> Unit) {
            lambda(-1)
            lambda(0)
            lambda(1)
        }

        minusOneOneZero { x ->
            minusOneOneZero { y ->
                minusOneOneZero { z ->
                    if (x != 0 || y != 0 || z != 0)
                        lambda(new.offset(x, y, z), old.offset(x, y, z))
                }
            }
        }
    }

    private fun move(
        level: ServerLevel,
        ship: ServerShip,
        new: BlockPos,
        old: BlockPos,
        modifications: (Pair<Level, BlockPos>) -> Unit
    ) {
        modifications(Pair(level, new))
        modifications(Pair(level, old))

        level.relocateBlock(old, new, ship)
    }

    fun tickAssemblyTasks() {
        if (tasks.isEmpty()) return

        var amount = 0
        var i = 0
        val maxPerTask = EurekaConfig.SERVER.assembliesPerTick / tasks.size
        val changed = mutableSetOf<Pair<Level, BlockPos>>()

        while (amount < EurekaConfig.SERVER.assembliesPerTick && tasks.isNotEmpty()) {
            val task = tasks[i]
            var tAmount = 0
            while (tAmount < maxPerTask && !task.todo.isEmpty) {
                val (to, from) = task.todo.pop()

                if (from.distSqr(task.center) > (MAX_SIZE * MAX_SIZE)) continue
                bfs(task.level, task.ship, to, from, task.todo, task.predicate, changed)
                tAmount++
            }

            if (task.todo.isEmpty) {
                tasks.remove(task)
                task.onDone()
            }

            amount += tAmount

            if (++i > tasks.size) i = 0
        }

        changed.forEach { (level, pos) ->
            level.updateNeighborsAt(pos, level.getBlockState(pos).block)
        }
    }

    fun clearAssemblyTasks() {
        tasks.clear()
    }

    private class AssemblyTask(
        val level: ServerLevel,
        val ship: ServerShip,
        val center: BlockPos,
        val predicate: (BlockState) -> Boolean,
        val onDone: () -> Unit
    ) {
        val todo = ObjectArrayList<Pair<BlockPos, BlockPos>>()
    }

    private val logger by logger()
}
