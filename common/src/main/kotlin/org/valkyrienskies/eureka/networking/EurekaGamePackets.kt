package org.valkyrienskies.eureka.networking

import net.minecraft.server.level.ServerPlayer
import org.valkyrienskies.core.impl.networking.simple.register
import org.valkyrienskies.core.impl.networking.simple.registerServerHandler
import org.valkyrienskies.eureka.EurekaItems
import org.valkyrienskies.mod.common.util.MinecraftPlayer

object EurekaGamePackets {
    fun register() {
        PacketSetTetherDistance::class.register()
    }

    fun registerHandlers() {
        PacketSetTetherDistance::class.registerServerHandler { tetherDistance, iPlayer ->
            val player = (iPlayer as MinecraftPlayer).player as ServerPlayer
            val heldItem = player.getItemInHand(tetherDistance.itemHandSlot)
            if (heldItem.item != EurekaItems.ENDER_TETHER.get()) {
                return@registerServerHandler
            }
            // TODO: Clamp this value to be between 0 and 100 or something, then store it in item NBT
            println("Server got packet with tetherDistance $tetherDistance from player $player")
        }
    }
}
