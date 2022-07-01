package org.valkyrienskies.eureka.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Blocks
import org.joml.Vector3i
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.mod.common.util.toBlockPos

object ShipAssembler {
    val AIR = Blocks.AIR.defaultBlockState()

    // TODO use dense packed to send updates
    // with a more optimized algorithm for bigger ships

    fun fillShip(level: ServerLevel, ship: ShipData, center: BlockPos) {
        val shipCenter = ship.chunkClaim.getCenterBlockCoordinates(Vector3i()).toBlockPos()

        val blockState = level.getBlockState(center)
        level.setBlock(center, AIR, 11)
        level.setBlock(shipCenter, blockState, 11)
        Direction.values()
            .forEach { forwardAxis(level, shipCenter.relative(it), center.relative(it), it) }
    }

    private fun forwardAxis(
        level: ServerLevel,
        shipPos: BlockPos,
        pos: BlockPos,
        direction: Direction
    ) {
        var pos = pos
        var shipPos = shipPos
        var blockState = level.getBlockState(pos)

        while (!BLOCK_BLACKLIST.contains(Registry.BLOCK.getKey(blockState.block).toString())) {
            level.setBlock(pos, AIR, 11)
            level.setBlock(shipPos, blockState, 11)

            Direction.values().filter { it != direction && it != direction.opposite }
                .forEach { forwardAxis(level, shipPos.relative(it), pos.relative(it), it) }

            pos = pos.relative(direction)
            shipPos = shipPos.relative(direction)
            blockState = level.getBlockState(pos)
        }
    }
}
