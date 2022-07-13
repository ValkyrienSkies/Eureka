package org.valkyrienskies.vs2api

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.core.game.ships.ShipObject
import org.valkyrienskies.core.game.ships.ShipObjectClient
import org.valkyrienskies.core.game.ships.ShipObjectServer
import org.valkyrienskies.eureka.ship.EurekaShipControl
import org.valkyrienskies.eureka.util.defineSynced
import org.valkyrienskies.eureka.util.getBlockPos
import org.valkyrienskies.eureka.util.putBlockPos
import org.valkyrienskies.eureka.util.registerSynced
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toVec3d

class SeatEntity(type: EntityType<SeatEntity>, level: Level) : Entity(type, level) {
    private val ship: ShipObject?
        get() = inShipPosition?.let {
            level.getShipObjectManagingPos(BlockPos(it.x, it.y, it.z))
        }

    var inShipPosition
        get() = IN_SHIP_POSITION.get(this)
        set(value) = IN_SHIP_POSITION.set(this, value)

    init {
        blocksBuilding = true
    }

    private fun niceInShipPosition() = inShipPosition?.toJOMLD()?.add(0.5, -1.0, 0.5)

    // We discard any position assignments as long we are on a ship
    override fun setPosRaw(x: Double, y: Double, z: Double) {
        val vec = ship?.shipData?.shipTransform?.shipToWorldMatrix?.transformPosition(niceInShipPosition())?.toVec3d()
        super.setPosRaw(vec?.x ?: x, vec?.y ?: y, vec?.z ?: z)
    }

    override fun tick() {
        super.tick()
        reapplyPosition()

        if (level.isClientSide) return
        this.controllingPassenger?.let { player ->
            player as Player

            (ship as ShipObjectServer?)?.getAttachment<EurekaShipControl>()?.apply {
                // xRot = player.xRot
                // yRot = player.yRot
                leftImpulse = player.xxa
                forwardImpulse = player.zza
            }
        }
    }

    override fun defineSynchedData() {
        registerSynced(IN_SHIP_POSITION, BlockPos.ZERO)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        inShipPosition = compound.getBlockPos("inShipPosition")
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putBlockPos("inShipPosition", inShipPosition)
    }

    override fun getControllingPassenger(): Entity? {
        return this.passengers.getOrNull(0)
    }

    override fun getAddEntityPacket(): Packet<*> {
        return ClientboundAddEntityPacket(this)
    }

    override fun addPassenger(passenger: Entity) {
        super.addPassenger(passenger)
        if (passenger is ServerPlayer) {
            passenger.setPos(this.x, this.y, this.z)
        }
    }

    // Client only
    fun clientRenderPos(): Vec3? =
        (ship as ShipObjectClient?)?.renderTransform?.shipToWorldMatrix?.transformPosition(
            inShipPosition?.toJOMLD()?.add(0.5, 1.5, 0.5)
        )
            ?.toVec3d()

    companion object {
        val IN_SHIP_POSITION = defineSynced<SeatEntity, BlockPos>(EntityDataSerializers.BLOCK_POS)
    }
}
