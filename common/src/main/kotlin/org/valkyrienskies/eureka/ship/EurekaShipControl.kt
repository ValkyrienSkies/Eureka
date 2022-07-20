package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Math.clamp
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.api.ShipUser
import org.valkyrienskies.core.api.Ticked
import org.valkyrienskies.core.api.shipValue
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.toJOMLD

private const val STABILIZATION_TORQUE_CONSTANT = 15.0
private const val STABILIZATION_FORCE_CONSTANT = 15.0
private const val TURN_SPEED = 4.0
private const val MAX_RISE_VEL = 2.5
private const val IMPULSE_ALLEVIATION_RATE = 2.3
private const val BASE_SPEED = 3.0

class EurekaShipControl : ShipForcesInducer, ShipUser, Ticked {

    @JsonIgnore
    override var ship: Ship? = null
    val controllingPlayer by shipValue<SeatedControllingPlayer>()

    private var extraForce = 0.0
    private var alleviationTarget = Double.NaN

    override fun applyForces(forcesApplier: ForcesApplier, physShip: PhysShip) {
        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val shipUp = Vector3d(0.0, 1.0, 0.0)
        val shipFront = Vector3d(0.0, 0.0, 1.0)
        val worldUp = Vector3d(0.0, 1.0, 0.0)

        physShip.rotation.transform(shipUp)
        physShip.rotation.transform(shipFront)

        run { // Stabilization
            val angleBetween = shipUp.angle(worldUp)
            if (angleBetween > .01) {
                val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, Vector3d()).normalize()
                val idealAngularAcceleration =
                    stabilizationRotationAxisNormalized.mul(angleBetween, stabilizationRotationAxisNormalized)
                // Only subtract the x/z components of omega. We still want to allow rotation along the Y-axis (yaw).
                idealAngularAcceleration.sub(
                    physShip.omega.x(),
                    0.0,
                    physShip.omega.z()
                )

                val stabilizationTorque = physShip.rotation.transform(
                    moiTensor.transform(
                        physShip.rotation.transformInverse(
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

        var linearStabilize = controllingPlayer == null

        controllingPlayer?.let { player ->
            // Player controlled rotation
            val rotationVector = Vector3d(
                0.0,
                if (player.leftImpulse != 0.0f)
                    (player.leftImpulse.toDouble() * TURN_SPEED)
                else
                    -physShip.omega.y() * STABILIZATION_TORQUE_CONSTANT,
                0.0
            )

            physShip.rotation.transform(
                moiTensor.transform(
                    physShip.rotation.transformInverse(
                        rotationVector,
                        rotationVector
                    )
                )
            )

            forcesApplier.applyInvariantTorque(rotationVector)

            // Player controlled forward and backward thrust
            val forwardVector = player.seatInDirection.normal.toJOMLD()
            forwardVector.mul(player.forwardImpulse.toDouble())
            physShip.rotation.transform(forwardVector)
            val idealForwardVel = Vector3d(forwardVector)
            idealForwardVel.mul(BASE_SPEED)
            val forwardVelInc = idealForwardVel.sub(physShip.velocity.x(), 0.0, physShip.velocity.z())
            forwardVelInc.mul(mass * 10)
            forwardVelInc.add(forwardVector.mul(extraForce))

            if (forwardVelInc.lengthSquared() < 0.1f) {
                linearStabilize = true
            }

            forcesApplier.applyInvariantForce(forwardVelInc)

            // Player controlled alleviation
            if (player.upImpulse != 0.0f)
                alleviationTarget = physShip.position.y() + (player.upImpulse * IMPULSE_ALLEVIATION_RATE)
        }

        // If player is null or if there is no linear impulse we will apply a linear force to stabilize the ship.
        if (linearStabilize) {
            val antiVel = Vector3d(physShip.velocity).negate()

            if (antiVel.lengthSquared() > STABILIZATION_FORCE_CONSTANT * STABILIZATION_FORCE_CONSTANT)
                antiVel.normalize().mul(STABILIZATION_FORCE_CONSTANT)

            forcesApplier.applyInvariantForce(antiVel)
        }

        if (alleviationTarget.isFinite()) {
            val diff = alleviationTarget - physShip.position.y()

            val shipRiseVelo = physShip.velocity.y()
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

    var power = 0.0

    override fun tick() {
        extraForce = power
        power = 0.0
    }
}
