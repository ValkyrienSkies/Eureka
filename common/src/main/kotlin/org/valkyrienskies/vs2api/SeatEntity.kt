package org.valkyrienskies.vs2api

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.game.ships.ShipDataCommon
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import java.util.Optional

class SeatEntity(type: EntityType<SeatEntity>, level: Level) : Entity(type, level) {
    var ship: ShipDataCommon? = null
        set(value) {
            if (value == field) return
            field = value
            shipPosition = position().toJOML()
            entityData.set(SHIP_DATA, Optional.ofNullable(value?.id))
            println("SHIP SET: ShipPos: $shipPosition")
            reapplyPosition()
            println("SHIP SET: RealPos: ${position()}")
        }

    private var shipPosition = position().toJOML()

    init {
        blocksBuilding = true
    }

    // We discard any position assignments as long we are on a ship
    override fun setPosRaw(x: Double, y: Double, z: Double) {
        val vec = ship?.shipTransform?.shipToWorldMatrix?.transformPosition(Vector3d(shipPosition))
        super.setPosRaw(vec?.x ?: x, vec?.y ?: y, vec?.z ?: z)
    }

    override fun tick() {
        super.tick()
        reapplyPosition()
        if (ship == null && entityData.get(SHIP_DATA).isPresent) {
            val ship = level.shipObjectWorld.queryableShipData.getById(entityData.get(SHIP_DATA).get())!!
            val inShip = ship.shipTransform.worldToShipMatrix.transformPosition(position().toJOML())
            setPosRaw(inShip.x, inShip.y, inShip.z)
            this.ship = ship
            // if (level.getShipManagingPos(this.blockPosition())!!.shipUUID != ship.shipUUID) println("Shouldn't happen REE")
        }
    }

    override fun defineSynchedData() {
        entityData.define(SHIP_DATA, Optional.empty())
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

    companion object {
        val SHIP_DATA = SynchedEntityData.defineId(
            this::class.java.declaringClass as Class<SeatEntity>,
            EntityDataSerializers.OPTIONAL_UUID
        )
    }
}
