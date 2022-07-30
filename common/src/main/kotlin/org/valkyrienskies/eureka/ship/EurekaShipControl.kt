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
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.toJOMLD

private const val MAX_RISE_VEL = 2.5

class EurekaShipControl : ShipForcesInducer, ShipUser, Ticked {

    @JsonIgnore
    override var ship: Ship? = null
    val controllingPlayer by shipValue<SeatedControllingPlayer>()

    private var extraForce = 0.0
    private var alleviationTarget = Double.NaN
    private var aligning = 0 // tries to align the ship in this amount of physticks
    private var cruiseSpeed = Double.NaN
    private val anchored get() = anchorsActive > 0
    private var wasAnchored = false

    override fun applyForces(forcesApplier: ForcesApplier, physShip: PhysShip) {
        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor

        // Revisiting eureka control code.
        // [x] Move torque stabilization code
        // [x] Move linear stabilization code
        // [x] Revisit player controlled torque
        // [x] Revisit player controlled linear force
        // [x] Anchor freezing
        // [ ] Rewrite Alignment code
        // [ ] Revisit Alleviation code
        // [ ] Balloon limiter
        // [ ] Add Cruise code

        // region Aligning
        /*
        if (aligning > 0) {

            val angle = shipFront.angle(worldFront)

            // value between 0 and 3 (inclusive) to horizontal direction
            val alignTo = Direction.from2DDataValue(floor((angle / (Math.PI / 2.0)) + 0.5).toInt())

            val linearDiff = Vector3d(physShip.position)
                .sub(
                    Vector3d(physShip.position)
                        .add(0.5, 0.5, 0.5)
                        .floor()
                        .add(0.5, 0.5, 0.0)
                )

            // Were gonna use a direction as sole input, cus this would work well with dissasembly
            val alignFront = alignTo.normal.toJOMLD()
            val angleAlign = shipFront.angle(alignFront)
            if (angleAlign < 0.01 && linearDiff.lengthSquared() < 0.1)
                aligning--
            else {
                // Torque
                val idealAlignAccel =
                    shipFront.cross(alignFront, alignFront).normalize().mul(angleAlign, alignFront)

                idealAlignAccel.sub(physShip.omega)

                val alignTorque = physShip.rotation.transform(
                    moiTensor.transform(
                        physShip.rotation.transformInverse(
                            idealAlignAccel,
                            idealAlignAccel
                        )
                    )
                )

                alignTorque.mul(STABILIZATION_TORQUE_CONSTANT)
                forcesApplier.applyInvariantTorque(alignTorque)

                // Linear
                linearStabilize = false

                val idealVelocity = linearDiff
                idealVelocity.sub(physShip.velocity)
                idealVelocity.mul(mass * 10)

                forcesApplier.applyInvariantForce(idealVelocity)
            }
        }
        */

        // endregion

        if (!anchored) {
            var linearStabilize = controllingPlayer == null

            stabilize(physShip, forcesApplier, linearStabilize, controllingPlayer == null)

            controllingPlayer?.let { player ->
                // region Player controlled rotation
                val rotationVector = Vector3d(
                    0.0,
                    if (player.leftImpulse != 0.0f)
                        (player.leftImpulse.toDouble() * EurekaConfig.SERVER.turnSpeed)
                    else
                        -physShip.omega.y() * EurekaConfig.SERVER.turnSpeed,
                    0.0
                )

                rotationVector.sub(0.0, physShip.omega.y(), 0.0)

                physShip.rotation.transform(
                    moiTensor.transform(
                        physShip.rotation.transformInverse(
                            rotationVector,
                            rotationVector
                        )
                    )
                )

                forcesApplier.applyInvariantTorque(rotationVector)
                // endregion

                // region Player controlled forward and backward thrust
                val forwardVector = player.seatInDirection.normal.toJOMLD()
                forwardVector.mul(player.forwardImpulse.toDouble())
                physShip.rotation.transform(forwardVector)
                val idealForwardVel = Vector3d(forwardVector)
                idealForwardVel.mul(EurekaConfig.SERVER.baseSpeed)
                val forwardVelInc = idealForwardVel.sub(physShip.velocity.x(), 0.0, physShip.velocity.z())
                forwardVelInc.mul(mass * 10)
                forwardVelInc.add(forwardVector.mul(extraForce))

                if (forwardVelInc.lengthSquared() < 0.1f) {
                    linearStabilize = true
                } else forcesApplier.applyInvariantForce(forwardVelInc)
                // endregion

                // Player controlled alleviation
                if (player.upImpulse != 0.0f)
                    alleviationTarget =
                        physShip.position.y() + (player.upImpulse * EurekaConfig.SERVER.impulseAlleviationRate)
            }

            // region Alleviation
            if (alleviationTarget.isFinite()) {
                val diff = alleviationTarget - physShip.position.y()

                val shipRiseVelo = physShip.velocity.y()
                val idealRiseVelo = clamp(diff, -MAX_RISE_VEL, MAX_RISE_VEL)
                val impulse = idealRiseVelo - shipRiseVelo

                forcesApplier.applyInvariantForce(Vector3d(0.0, impulse * mass * 10, 0.0))
            }
            // endregion
        } else if (wasAnchored != anchored) {
            forcesApplier.setStatic(anchored)
        }

        // Drag
        forcesApplier.applyInvariantForce(Vector3d(physShip.velocity.y()).mul(-mass))
    }

    var power = 0.0

    override fun tick() {
        extraForce = power
        power = 0.0
    }

    fun align() {
        if (aligning == 0)
            aligning += 60
    }

    var anchors = 0 // Amount of anchors
    var anchorsActive = 0 // Anchors that are active
    var balloons = 0 // Amount of balloons
}
