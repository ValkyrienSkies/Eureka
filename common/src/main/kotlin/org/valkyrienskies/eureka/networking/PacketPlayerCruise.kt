package org.valkyrienskies.mod.common.networking

import org.valkyrienskies.core.networking.simple.SimplePacket

data class PacketPlayerCruise(val cruise: Boolean) : SimplePacket
