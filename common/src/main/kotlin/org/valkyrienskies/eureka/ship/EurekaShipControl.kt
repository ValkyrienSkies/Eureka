package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.api.ShipUser
import org.valkyrienskies.core.game.ships.ShipObjectServer

private const val STABILIZATION_TORQUE_CONSTANT = 7.5
private const val RISE_ACC = 0.1
private const val MAX_RISE_ACC = 2.0

class EurekaShipControl : ShipForcesInducer, ShipUser {

    @JsonIgnore
    override var ship: Ship? = null
    var alleviationTarget: Double = 30.0

    override fun applyForces(forcesApplier: ForcesApplier) {
        val shipData = (ship as ShipObjectServer).shipData
        val mass = shipData.inertiaData.getShipMass()
        val shipUp = Vector3d(0.0, 1.0, 0.0)
        val worldUp = Vector3d(0.0, 1.0, 0.0)

        shipData.shipTransform.shipToWorldMatrix.transformDirection(shipUp)

        val angleBetween = shipUp.angle(worldUp)
        if (angleBetween > .01) {
            val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, worldUp).normalize()
            val stabilizationTorque = shipData.inertiaData.getMomentOfInertiaTensor()
                .transform(stabilizationRotationAxisNormalized.mul(angleBetween, Vector3d()))
            // stabilizationTorque.mul(1.0 / 60.0)
            stabilizationTorque.mul(STABILIZATION_TORQUE_CONSTANT)

            forcesApplier.applyInvariantTorque(stabilizationTorque)
        }

        if (alleviationTarget != Double.NaN) {
        }

        forcesApplier.applyInvariantForce(Vector3d(0.0, mass * 10, 0.0))
    }
}
