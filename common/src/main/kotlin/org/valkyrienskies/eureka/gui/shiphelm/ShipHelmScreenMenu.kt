package org.valkyrienskies.eureka.gui.shiphelm

import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import org.valkyrienskies.eureka.EurekaScreens
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

class ShipHelmScreenMenu(syncId: Int, playerInv: Inventory, var blockEntity: ShipHelmBlockEntity?) :
    AbstractContainerMenu(EurekaScreens.SHIP_HELM.get(), syncId) {

    constructor(syncId: Int, playerInv: Inventory) : this(syncId, playerInv, null)

    val assembled = blockEntity?.assembled ?: false

    override fun stillValid(player: Player): Boolean = true

    override fun clickMenuButton(player: Player, id: Int): Boolean {
        if (blockEntity == null) return false

        if (id == 0 && !assembled && !player.level.isClientSide) {
            blockEntity = blockEntity!!.assemble() ?: throw IllegalStateException()
            blockEntity!!.sit(player, true)
            return true
        }

        if (id == 1 && assembled && !player.level.isClientSide) {
            blockEntity!!.align()
            return true
        }

        return super.clickMenuButton(player, id)
    }

    companion object {
        val factory: (syncId: Int, playerInv: Inventory) -> ShipHelmScreenMenu = ::ShipHelmScreenMenu
    }
}
