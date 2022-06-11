package org.valkyrienskies.vs2api

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level

class SeatEntity(type: EntityType<SeatEntity>, level: Level) : Entity(type, level) {

    init {
        blocksBuilding = true
    }

    override fun defineSynchedData() {
        // TODO("Not yet implemented")
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        // TODO("Not yet implemented")
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        // TODO("Not yet implemented")
    }

    override fun getControllingPassenger(): Entity? {
        return this.passengers.getOrNull(0)
    }

    override fun getAddEntityPacket(): Packet<*> {
        return ClientboundAddEntityPacket(this)
    }
}
