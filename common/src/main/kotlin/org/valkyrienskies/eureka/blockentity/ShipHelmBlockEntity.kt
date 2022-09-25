package org.valkyrienskies.eureka.blockentity

import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.TickableBlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.Half
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.core.api.saveAttachment
import org.valkyrienskies.core.game.ships.ShipData
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.api.ShipBlockEntity
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toDoubles
import org.valkyrienskies.mod.common.util.toJOML

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider, ShipBlockEntity, TickableBlockEntity {

    override var ship: ServerShip? = null // TODO ship is not being set in vs2?
        get() = field ?: (level as ServerLevel).getShipObjectManagingPos(this.blockPos)
    val assembled get() = ship != null
    var shouldDisassembleWhenPossible = false

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory, this)
    }

    override fun getDisplayName(): Component {
        return TextComponent("Ship Helm")
    }

    // Needs to get called server-side
    fun spawnSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel): ShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        val newShape = newState.getShape(level, newPos)
        val newBlock = newState.block
        var height = 0.0
        if (!newState.isAir) {
            height = if (
                newBlock is StairBlock &&
                (!newState.hasProperty(StairBlock.HALF) || newState.getValue(StairBlock.HALF) == Half.BOTTOM)
            )
                0.5 // Valid StairBlock
            else
                newShape.max(Axis.Y)
        }
        val entity = ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE.create(level)!!.apply {
            val seatEntityPos: Vector3dc = Vector3d(newPos.x + .5, (newPos.y - .5) + height, newPos.z + .5)
            inShipPosition = seatEntityPos
            if (ship != null) {
                val posInWorld = ship!!.shipToWorld.transformPosition(seatEntityPos, Vector3d())
                moveTo(posInWorld.x, posInWorld.y, posInWorld.z)
            } else {
                moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())
            }

            lookAt(
                EntityAnchorArgument.Anchor.EYES,
                state.getValue(HorizontalDirectionalBlock.FACING).normal.toDoubles().add(position())
            )

            isController = true
        }

        level.addFreshEntityWithPassengers(entity)
        return entity
    }

    // Needs to get called server-side
    fun assemble() {
        val level = level as ServerLevel

        // Check the block state before assembling to avoid creating an empty ship
        val blockState = level.getBlockState(blockPos)
        if (blockState.block != ShipHelmBlock) {
            return
        }

        val ship: ShipData =
            level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)
        ship.saveAttachment(EurekaShipControl())
        ShipAssembler.fillShip(
            level,
            ship,
            blockPos
        ) { !EurekaConfig.SERVER.blockBlacklist.contains(Registry.BLOCK.getKey(it.block).toString()) }
    }

    override fun tick() {
        if (shouldDisassembleWhenPossible && ship?.getAttachment<EurekaShipControl>()?.canDisassemble == true) {
            this.disassemble()
        }
    }

    fun disassemble() {
        val ship = ship ?: return
        val level = level ?: return

        val control = ship.getAttachment<EurekaShipControl>() ?: return
        if (!control.canDisassemble) {
            shouldDisassembleWhenPossible = true
            control.aligning = true
            return
        }

        val shipToWorld = ship.shipToWorld
        val temp0 = Vector3d()
        val temp1 = MutableBlockPos()
        ship.shipActiveChunksSet.iterateChunkPos { chunkX, chunkZ ->
            val chunk = level.getChunk(chunkX, chunkZ)
            for (section in chunk.sections) {
                if (section == null) continue
                for (x in 0 .. 15) {
                    for (y in 0 .. 15) {
                        for (z in 0 .. 15) {
                            val state = section.getBlockState(x, y, z)
                            if (state.isAir) continue

                            val realX = (chunkX shl 4) + x
                            val realY = section.bottomBlockY() + y
                            val realZ = (chunkZ shl 4) + z


                            val inWorldPos = shipToWorld.transformPosition(
                                temp0.set(realX.toDouble(), realY.toDouble(), realZ.toDouble())).round();
                            val inWorldBlockPos = temp1.set(inWorldPos.x.toInt(), inWorldPos.y.toInt(), inWorldPos.z.toInt())
                            level.setBlock(inWorldBlockPos, state, 2)

                            val realPos = temp1.set(realX, realY, realZ)
                            level.setBlock(realPos, Blocks.AIR.defaultBlockState(), 2)
                        }
                    }
                }
            }
        }
        shouldDisassembleWhenPossible = false
    }

    fun align() {
        val control = ship?.getAttachment<EurekaShipControl>() ?: return
        control.aligning = !control.aligning
    }

    fun sit(player: Player, force: Boolean = false): Boolean =
        player.startRiding(spawnSeat(blockPos, blockState, level as ServerLevel), force)

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }
}
