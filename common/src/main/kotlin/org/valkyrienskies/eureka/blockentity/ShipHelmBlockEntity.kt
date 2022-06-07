package org.valkyrienskies.eureka.blockentity

import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu

class ShipHelmBlockEntity: BlockEntity(EurekaBlockEntities.SHIP_HELM.get()), MenuProvider {

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }

    override fun createMenu(id: Int, playerInventory: Inventory, player: Player): AbstractContainerMenu {
        return ShipHelmScreenMenu(id, playerInventory)
    }

    override fun getDisplayName(): Component {
        return Component.nullToEmpty("Ship Helm")
    }
}