package org.valkyrienskies.eureka.blockentity

import net.minecraft.Util
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING
import net.minecraft.world.level.block.state.properties.Half
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.impl.util.logger
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.common.ValkyrienSkiesMod
import org.valkyrienskies.mod.common.entity.ShipMountingEntity
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toDoubles
import org.valkyrienskies.mod.common.util.toJOMLD

class ShipHelmBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get(), pos, state), MenuProvider {

    private val ship: ServerShip? get() = (level as ServerLevel).getShipObjectManagingPos(this.blockPos)
    private val control: EurekaShipControl? get() = ship?.getAttachment(EurekaShipControl::class.java)
    private val seats = mutableListOf<ShipMountingEntity>()
    val assembled get() = ship != null
    val aligning get() = control?.aligning ?: false
    private var shouldDisassembleWhenPossible = false

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory, this)
    }

    override fun getDisplayName(): Component {
        return TranslatableComponent("gui.vs_eureka.ship_helm")
    }

    // Needs to get called server-side
    fun spawnSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel): ShipMountingEntity {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val newState = level.getBlockState(newPos)
        val newShape = newState.getShape(level, newPos)
        val newBlock = newState.block
        var height = 0.5
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
            moveTo(seatEntityPos.x(), seatEntityPos.y(), seatEntityPos.z())

            lookAt(
                EntityAnchorArgument.Anchor.EYES,
                state.getValue(HORIZONTAL_FACING).normal.toDoubles().add(position())
            )

            isController = true
        }

        level.addFreshEntityWithPassengers(entity)
        return entity
    }

    fun startRiding(player: Player, force: Boolean, blockPos: BlockPos, state: BlockState, level: ServerLevel): Boolean {
        for (i in seats.size - 1 downTo 0) {
            if (!seats[i].isVehicle) {
                seats[i].kill()
                seats.removeAt(i)
            } else if (!seats[i].isAlive) {
                seats.removeAt(i)
            }
        }

        val seat = spawnSeat(blockPos, blockState, level)
        val ride = player.startRiding(seat, force)

        if (ride) {
            control?.seatedPlayer = player
            seats.add(seat)
        }

        return ride
    }

    fun tick() {
        if (shouldDisassembleWhenPossible && ship?.getAttachment<EurekaShipControl>()?.canDisassemble == true) {
            this.disassemble()
        }
        control?.ship = ship
    }

    // Needs to get called server-side
    fun assemble(player: Player) {
        val level = level as ServerLevel

        // Check the block state before assembling to avoid creating an empty ship
        val blockState = level.getBlockState(blockPos)
        if (blockState.block !is ShipHelmBlock) return

        val builtShip = ShipAssembler.collectBlocks(
            level,
            blockPos
        ) { !it.isAir && !EurekaConfig.SERVER.blockBlacklist.contains(Registry.BLOCK.getKey(it.block).toString()) }

        if (builtShip == null) {
            player.sendMessage(TextComponent("Ship is too big! Max size is ${EurekaConfig.SERVER.maxShipBlocks} blocks (changeable in the config)"), Util.NIL_UUID)
            logger.warn("Failed to assemble ship for ${player.name.string}")
        }
    }

    fun disassemble() {
        val ship = ship ?: return
        val level = level ?: return
        val control = control ?: return

        if (!control.canDisassemble) {
            shouldDisassembleWhenPossible = true
            control.disassembling = true
            control.aligning = true
            return
        }

        val inWorld = ship.shipToWorld.transformPosition(this.blockPos.toJOMLD())

        ShipAssembler.unfillShip(
            level as ServerLevel,
            ship,
            this.blockPos,
            BlockPos(inWorld.x, inWorld.y, inWorld.z)
        )
        // ship.die() TODO i think we do need this no? or autodetecting on all air

        shouldDisassembleWhenPossible = false
    }

    fun align() {
        val control = control ?: return
        control.aligning = !control.aligning
    }

    override fun setRemoved() {
        if (level?.isClientSide == false) {
            for (i in seats.indices) {
                seats[i].kill()
            }
            seats.clear()
        }

        super.setRemoved()
    }

    fun sit(player: Player, force: Boolean = false): Boolean {
        // If player is already controlling the ship, open the helm menu
        if (!force && player.vehicle?.type == ValkyrienSkiesMod.SHIP_MOUNTING_ENTITY_TYPE && seats.contains(player.vehicle as ShipMountingEntity)) {
            player.openMenu(this)
            return true
        }

        // val seat = spawnSeat(blockPos, blockState, level as ServerLevel)
        // control?.seatedPlayer = player
        // return player.startRiding(seat, force)
        return startRiding(player, force, blockPos, blockState, level as ServerLevel)
    }
    private val logger by logger()
}
