package org.valkyrienskies.eureka.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.EurekaEntities
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu
import org.valkyrienskies.vs2api.SeatEntity

class ShipHelmBlockEntity :
    BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider {

    lateinit var seat: SeatEntity // TODO we should only create it when it doesn't exist yet

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }

    override fun setLevelAndPosition(level: Level, blockPos: BlockPos) {
        super.setLevelAndPosition(level, blockPos)
        if (::seat.isInitialized)
            seat.remove() // remove old if we make new seat

        seat = EurekaEntities.SEAT.get().create(level)!!.apply {
            setPosition(
                this@ShipHelmBlockEntity.blockPos.relative(
                    this@ShipHelmBlockEntity.blockState.getValue(
                        HorizontalDirectionalBlock.FACING
                    )
                )
            )
        }
    }

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory)
    }

    override fun getDisplayName(): Component {
        return Component.nullToEmpty("Ship Helm")
    }
}
