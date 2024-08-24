package org.valkyrienskies.eureka.networking

import net.minecraft.world.InteractionHand
import org.valkyrienskies.core.impl.networking.simple.SimplePacket

data class PacketSetTetherDistance(
    val tetherDistance: Double,
    val itemHandSlot: InteractionHand,
) : SimplePacket
