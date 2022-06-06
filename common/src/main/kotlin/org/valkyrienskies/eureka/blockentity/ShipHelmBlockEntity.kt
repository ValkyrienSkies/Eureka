package org.valkyrienskies.eureka.blockentity

import net.minecraft.block.entity.BlockEntity
import org.valkyrienskies.eureka.EurekaBlockEntities

class ShipHelmBlockEntity: BlockEntity(EurekaBlockEntities.SHIP_HELM.get()) {

    init {
        println("Entity exists")
    }

    companion object {
        val supplier = { ShipHelmBlockEntity() }
    }
}