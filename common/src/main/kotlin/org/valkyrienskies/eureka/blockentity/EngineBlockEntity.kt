package org.valkyrienskies.eureka.blockentity

import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.entity.BlockEntity
import org.valkyrienskies.eureka.EurekaBlockEntities
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu

class EngineBlockEntity : BlockEntity(EurekaBlockEntities.ENGINE.get()), MenuProvider {

    override fun createMenu(containerId: Int, inventory: Inventory, player: Player): AbstractContainerMenu =
        EngineScreenMenu(containerId, inventory, this)

    override fun getDisplayName(): Component = Component.nullToEmpty("Ship Engine")

    companion object {
        val supplier = { EngineBlockEntity() }
    }
}
