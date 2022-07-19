package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.api.saveAttachment
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaEntities
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.api.ShipBlockEntity
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.vs2api.SeatEntity
import java.util.UUID

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider, ShipBlockEntity {

    private var seatUuid: UUID? = null
    var seat: SeatEntity? = null
        get() = field ?: ((level as ServerLevel).getEntity(seatUuid) as SeatEntity)
            .apply { field = this }
        set(value) {
            field = value
            seatUuid = value?.uuid
        }

    override var ship: Ship? = null
    val assembled get() = ship != null

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.nullToEmpty("Ship Helm")
    }

    override fun save(tag: CompoundTag): CompoundTag {
        tag.putUUID("Seat", seat?.uuid ?: seatUuid)
        return super.save(tag)
    }

    override fun load(blockState: BlockState, tag: CompoundTag) {
        if (tag.contains("Seat") && seatUuid == null)
            seatUuid = tag.getUUID("Seat")
        super.load(blockState, tag)
    }

    // Needs to get called server-side
    fun initSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel) {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING))
        val entity = EurekaEntities.SEAT.get().create(level)!!.apply {
            moveTo(newPos.below(), 0f, 0f)
            inShipPosition = newPos
        }
        level.addFreshEntityWithPassengers(entity)
        seat = entity
        entity.tick()
    }

    // Needs to get called server-side
    fun onAssemble() {
        val level = level as ServerLevel

        // Check the block state before assembling to avoid creating an empty ship
        val blockState = level.getBlockState(blockPos)
        if (blockState.block != ShipHelmBlock) {
            return
        }

        val ship = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, level.dimensionId)
        ship.saveAttachment(EurekaShipControl())
        ShipAssembler.fillShip(level, ship, blockPos)
    }

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }
}
