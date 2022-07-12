package org.valkyrienskies.eureka.ship

import org.joml.Math.clamp
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.game.ships.PhysShip

private const val STABILIZATION_TORQUE_CONSTANT = 15.0
private const val TURN_SPEED = 3.0
private const val RISE_ACC = 0.1
private const val MAX_RISE_ACC = 2.0

class EurekaShipControl : ShipForcesInducer {

    var leftImpulse = 0.0f
    var forwardImpulse = 0.0f
    var maxSpeed = 20.0f
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
            val idealAngularAcceleration =
                stabilizationRotationAxisNormalized.mul(angleBetween, stabilizationRotationAxisNormalized)
            // Only subtract the x/z components of omega. We still want to allow rotation along the Y-axis (yaw).
            idealAngularAcceleration.sub(ship.omega.x(), 0.0, ship.omega.z())

            val stabilizationTorque = ship.rotation.transform(
                moiTensor.transform(
                    ship.rotation.transformInverse(
                        idealAngularAcceleration,
                        idealAngularAcceleration
                    )
                )
            )
            // stabilizationTorque.mul(1.0 / 60.0)
            stabilizationTorque.mul(STABILIZATION_TORQUE_CONSTANT)
            forcesApplier.applyInvariantTorque(stabilizationTorque)
        }

        if (alleviationTarget != Double.NaN) {
        }

        // forcesApplier.applyInvariantForce(Vector3d(0.0, mass * 10, 0.0))

        val forceMul = clamp(0.0, 0.1, 1 - (ship.velocity.length() / maxSpeed)) * 10.0
        val forwardPower = mass * 10 * forwardImpulse * forceMul
        forcesApplier.applyRotDependentForce(Vector3d(0.0, 0.0, forwardPower))

        forcesApplier.applyInvariantTorque(moiTensor.transform(Vector3d(0.0, leftImpulse.toDouble() * TURN_SPEED, 0.0)))
    }
}
