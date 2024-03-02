package org.valkyrienskies.eureka.ship

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.eureka.EurekaConfig
import kotlin.math.atan

fun stabilize(
    ship: PhysShipImpl,
    omega: Vector3dc,
    vel: Vector3dc,
    forces: PhysShip,
    linear: Boolean,
    yaw: Boolean
) {
    val shipUp = Vector3d(0.0, 1.0, 0.0)
    val worldUp = Vector3d(0.0, 1.0, 0.0)
    ship.poseVel.rot.transform(shipUp)

    val angleBetween = shipUp.angle(worldUp)
    val idealAngularAcceleration = Vector3d()
    if (angleBetween > .01) {
        val stabilizationRotationAxisNormalized = shipUp.cross(worldUp, Vector3d()).normalize()
        idealAngularAcceleration.add(
            stabilizationRotationAxisNormalized.mul(
                angleBetween,
                stabilizationRotationAxisNormalized
            )
        )
    }

    // Only subtract the x/z components of omega.
    // We still want to allow rotation along the Y-axis (yaw).
    // Except if yaw is true, then we stabilize
    idealAngularAcceleration.sub(
        omega.x(),
        if (!yaw) 0.0 else omega.y(),
        omega.z()
    )

    val stabilizationTorque = ship.poseVel.rot.transform(
        ship.inertia.momentOfInertiaTensor.transform(
            ship.poseVel.rot.transformInverse(idealAngularAcceleration)
        )
    )

    stabilizationTorque.mul(EurekaConfig.SERVER.stabilizationTorqueConstant)
    forces.applyInvariantTorque(stabilizationTorque)

    if (linear) {
        val idealVelocity = Vector3d(vel).negate()
        idealVelocity.y = 0.0

        // ideally this should work the same way as input is scaled
        val s = EurekaConfig.SERVER.linearStabilizeMaxAntiVelocity * (1 - 1 / smoothingATanMax(EurekaConfig.SERVER.linearMaxMass, ship.inertia.shipMass * EurekaConfig.SERVER.linearMassScaling + 1.0)) / 10.0

        if (idealVelocity.lengthSquared() > s * s)
            idealVelocity.normalize(s)

        idealVelocity.mul(ship.inertia.shipMass * (10 - EurekaConfig.SERVER.antiVelocityMassRelevance))
        forces.applyInvariantForce(idealVelocity)
    }
}

private fun smoothingATan(smoothing: Double, x: Double): Double = atan(x * smoothing) / smoothing
private fun smoothingATanMax(max: Double, x: Double): Double = smoothingATan(1 / (max * 0.638), x)
