package org.valkyrienskies.eureka.ship

import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.ShipForcesInducer

class EurekaShipControl : ShipForcesInducer {

    override fun applyForces(forcesApplier: ForcesApplier) {
        // forcesApplier.addInvariantForce(Vector3d(0.0, 10010.0, 0.0))
    }
}
