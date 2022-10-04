package org.valkyrienskies.mod.common.networking

import net.minecraft.server.level.ServerPlayer
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.core.api.setAttachment
import org.valkyrienskies.core.game.ships.ShipObjectServer
import org.valkyrienskies.core.networking.simple.register
import org.valkyrienskies.core.networking.simple.registerServerHandler
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.entity.EurekaShipMountingEntity
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.MinecraftPlayer

object EurekaGamePackets {

    fun register() {
        PacketPlayerCruise::class.register()
    }

    fun registerHandlers() {
        PacketPlayerCruise::class.registerServerHandler { driving, iPlayer ->
            val player = (iPlayer as MinecraftPlayer).player as ServerPlayer
            if (player.vehicle is EurekaShipMountingEntity && (player.vehicle as EurekaShipMountingEntity).isController) {
                val seat = player.vehicle!! as EurekaShipMountingEntity
                val ship = seat.level.getShipObjectManagingPos(seat.blockPosition())!! as ShipObjectServer
                val attachment =
                    ship.getAttachment() ?: EurekaShipControl()
                        .apply { ship.setAttachment(this) }

                attachment.cruiseOn = driving.cruise
            }
        }
    }
}
