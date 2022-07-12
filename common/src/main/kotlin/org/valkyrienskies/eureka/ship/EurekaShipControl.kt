package org.valkyrienskies.eureka.ship

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.game.ships.PhysShip

private const val STABILIZATION_TORQUE_CONSTANT = 7.5
private const val RISE_ACC = 0.1
private const val MAX_RISE_ACC = 2.0

class EurekaShipControl : ShipForcesInducer {

    var alleviationTarget: Double = 30.0

    override fun applyForces(forcesApplier: ForcesApplier, ship: PhysShip) {
        val mass = ship.inertia.shipMass
        val moiTensor = ship.inertia.momentOfInertiaTensor
        val shipUp = Vector3d(0.0, 1.0, 0.0)
        val worldUp = Vector3d(0.0, 1.0, 0.0)

        ship.rotation.transform(shipUp)

        val angleBetween = shipUp.angle(worldUp)
        if (angleBetween > .01) {
            val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, worldUp).normalize()
            val idealAngularAcceleration: Vector3dc = stabilizationRotationAxisNormalized.mul(angleBetween, Vector3d())

            val stabilizationTorque = ship.rotation.transform(
                moiTensor.transform(ship.rotation.transformInverse(idealAngularAcceleration, Vector3d()))
            )
            // stabilizationTorque.mul(1.0 / 60.0)
            stabilizationTorque.mul(STABILIZATION_TORQUE_CONSTANT)
            forcesApplier.applyInvariantTorque(stabilizationTorque)
        }

        if (alleviationTarget != Double.NaN) {
        }

        forcesApplier.applyInvariantForce(Vector3d(0.0, mass * 10, 0.0))
    }
}
