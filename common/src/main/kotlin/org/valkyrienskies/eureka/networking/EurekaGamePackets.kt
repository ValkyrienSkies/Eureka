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
        PacketSetTetherDistance::class.registerServerHandler { tetherDistancePacket, iPlayer ->
            val player = (iPlayer as MinecraftPlayer).player as ServerPlayer
            val heldItemStack = player.getItemInHand(tetherDistancePacket.itemHandSlot)
            if (heldItemStack.item != EurekaItems.ENDER_TETHER.get()) {
                return@registerServerHandler
            }
            EurekaItems.ENDER_TETHER.get()
                .setTetherDistance(heldItemStack, player.inventory, tetherDistancePacket.tetherDistance)
        }
    }
}
