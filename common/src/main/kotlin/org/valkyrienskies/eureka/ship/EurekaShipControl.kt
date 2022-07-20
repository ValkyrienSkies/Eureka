package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Math.clamp
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.api.ShipUser
import org.valkyrienskies.core.api.shipValue
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.toJOMLD

private const val STABILIZATION_TORQUE_CONSTANT = 15.0
private const val TURN_SPEED = 3.0
private const val MAX_RISE_VEL = 1.0
private const val IMPULSE_ALLEVIATION_RATE = 0.1f

class EurekaShipControl : ShipForcesInducer, ShipUser {

    @JsonIgnore
    override var ship: Ship? = null
    val controllingPlayer by shipValue<SeatedControllingPlayer>()

    var maxSpeed = 10.0f
    var alleviationTarget = 30.0f

    override fun applyForces(forcesApplier: ForcesApplier, ship: PhysShip) {
        val mass = ship.inertia.shipMass
        val moiTensor = ship.inertia.momentOfInertiaTensor
        val shipUp = Vector3d(0.0, 1.0, 0.0)
        val shipFront = Vector3d(0.0, 0.0, 1.0)
        val worldUp = Vector3d(0.0, 1.0, 0.0)

        ship.rotation.transform(shipUp)
        ship.rotation.transform(shipFront)

        run { // Stabilization
            val angleBetween = shipUp.angle(worldUp)
            if (angleBetween > .01) {
                val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, Vector3d()).normalize()
                val idealAngularAcceleration =
                    stabilizationRotationAxisNormalized.mul(angleBetween, stabilizationRotationAxisNormalized)
                // Only subtract the x/z components of omega. We still want to allow rotation along the Y-axis (yaw).
                idealAngularAcceleration.sub(
                    ship.omega.x(),
                    0.0,
                    ship.omega.z()
                )

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
        }

        controllingPlayer?.let { player ->
            // Player controlled rotation
            val rotationVector = Vector3d(
                0.0,
                if (player.leftImpulse != 0.0f)
                    player.leftImpulse.toDouble() * TURN_SPEED
                else
                    -ship.omega.y() * STABILIZATION_TORQUE_CONSTANT,
                0.0
            )
            ship.rotation.transform(
                moiTensor.transform(
                    ship.rotation.transformInverse(
                        rotationVector,
                        rotationVector
                    )
                )
            )
            forcesApplier.applyInvariantTorque(rotationVector)

            val idealForwardVel = player.seatInDirection.normal.toJOMLD()
            ship.rotation.transform(idealForwardVel)
            idealForwardVel.mul(player.forwardImpulse.toDouble() * maxSpeed)
            idealForwardVel.sub(ship.velocity.x(), 0.0, ship.velocity.z())
            idealForwardVel.mul(mass * 10)

            forcesApplier.applyInvariantForce(idealForwardVel)

            alleviationTarget += player.upImpulse * IMPULSE_ALLEVIATION_RATE
        }

        if (alleviationTarget.isFinite()) {
            val diff = alleviationTarget - ship.position.y()

            val shipRiseVelo = ship.velocity.y()
            val idealRiseVelo = clamp(diff, -MAX_RISE_VEL, MAX_RISE_VEL)
            val impulse = idealRiseVelo - shipRiseVelo

            // so if i remeber correcly newton told me
            //         acc=force/mass
            // and vel(t) = vel(t-1) + acc
            // so vel(t) = ship.velocity.y() + myInput/mass
            // so vel(t) = ship.velocity.y() + (myInput2*mass)/mass
            forcesApplier.applyInvariantForce(Vector3d(0.0, impulse * mass * 10, 0.0))
        }
    }
}
