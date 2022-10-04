package org.valkyrienskies.mod.common.entity

import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Vector3f
import org.valkyrienskies.core.networking.simple.sendToServer
import org.valkyrienskies.mod.common.config.EurekaKeyBindings
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.networking.PacketPlayerCruise
import org.valkyrienskies.mod.common.networking.PacketPlayerDriving

class EurekaShipMountingEntity(type: EntityType<ShipMountingEntity>, level: Level) : ShipMountingEntity(type, level) {


    override fun tick() {
        super.tick()
        if (level.getShipObjectManagingPos(blockPosition()!!) != null)
            sendCruisingPacket()
    }

    private fun sendCruisingPacket() {
        if (!level.isClientSide) return
        val cruise = EurekaKeyBindings.CRUISE_MAPPING.isDown

        PacketPlayerCruise(cruise).sendToServer()
    }
}
