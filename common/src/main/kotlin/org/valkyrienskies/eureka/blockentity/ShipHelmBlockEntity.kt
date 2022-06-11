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
import org.valkyrienskies.vs2api.SeatEntity
import java.util.UUID

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider {

    private var seatUuid: UUID? = null
    val seat by lazy { (level as ServerLevel).getEntity(seatUuid) as SeatEntity }

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory)
    }

    override fun getDisplayName(): Component {
        return Component.nullToEmpty("Ship Helm")
    }

    override fun save(tag: CompoundTag): CompoundTag {
        tag.putUUID("Seat", seat.uuid ?: seatUuid)
        return super.save(tag)
    }

    override fun load(blockState: BlockState, tag: CompoundTag) {
        seatUuid = tag.getUUID("Seat")
        super.load(blockState, tag)
    }

    fun initSeat(blockPos: BlockPos, state: BlockState, level: ServerLevel) {
        val newPos = blockPos.relative(state.getValue(HorizontalDirectionalBlock.FACING)).below()
        val entity = EurekaEntities.SEAT.get().create(level)!!.apply { moveTo(newPos, 0f, 0f) }
        level.addFreshEntityWithPassengers(entity)
        seatUuid = entity.uuid
    }
}
