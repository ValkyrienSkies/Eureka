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
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaEntities
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.eureka.util.ShipAssembler
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.vs2api.SeatEntity
import java.util.UUID

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider {

    private var seatUuid: UUID? = null
    val seat by lazy {
        val r = (level as ServerLevel).getEntity(seatUuid) as SeatEntity
        if (assembled)
            r.ship = (level as ServerLevel).getShipManagingPos(blockPos)!!

        r
    }

    val assembled get() = level?.getShipManagingPos(blockPos) != null

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory, this)
    }

    override fun getDisplayName(): Component {
        return Component.nullToEmpty("Ship Helm")
    }

    override fun save(tag: CompoundTag): CompoundTag {
        tag.putUUID("Seat", seat.uuid ?: seatUuid)
        return super.save(tag)
    }

    override fun load(blockState: BlockState, tag: CompoundTag) {
        if (tag.contains("Seat"))
            seatUuid = tag.getUUID("Seat")
        super.load(blockState, tag)
    }

    // Needs to get called server-side
    fun initSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel) {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING)).below()
        val entity = EurekaEntities.SEAT.get().create(level)!!.apply { moveTo(newPos, 0f, 0f) }
        level.addFreshEntityWithPassengers(entity)
        seatUuid = entity.uuid
        seat.position() // To get the lazy to act
    }

    // Needs to get called server-side
    fun onAssemble() {
        val level = level as ServerLevel
        val ship = level.shipObjectWorld.createNewShipAtBlock(blockPos.toJOML(), false, 1.0, 0)
        ShipAssembler.fillShip(level, ship, blockPos)
        seat.ship = ship
    }

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }
}
