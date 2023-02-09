package org.valkyrienskies.eureka.ship

import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.physics_api.SegmentDisplacement

fun stabilize(
    ship: PhysShipImpl,
    omega: Vector3dc,
    vel: Vector3dc,
    segment: SegmentDisplacement,
    forces: PhysShip,
    linear: Boolean,
    yaw: Boolean
) {
    val shipUp = Vector3d(0.0, 1.0, 0.0)
    val worldUp = Vector3d(0.0, 1.0, 0.0)
    SegmentUtils.transformDirectionWithoutScale(ship.poseVel, segment, shipUp, shipUp)

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

    val stabilizationTorque = SegmentUtils.transformDirectionWithScale(
        ship.poseVel,
        segment,
        ship.inertia.momentOfInertiaTensor.transform(
            SegmentUtils.invTransformDirectionWithScale(
                ship.poseVel,
                segment,
                idealAngularAcceleration,
                idealAngularAcceleration
            )
        ),
        idealAngularAcceleration
    )

    stabilizationTorque.mul(EurekaConfig.SERVER.stabilizationTorqueConstant)
    forces.applyInvariantTorque(stabilizationTorque)

    if (linear) {
        val idealVelocity = Vector3d(vel).negate()
        idealVelocity.y = 0.0

        if (idealVelocity.lengthSquared() > (EurekaConfig.SERVER.linearStabilizeMaxAntiVelocity * EurekaConfig.SERVER.linearStabilizeMaxAntiVelocity))
            idealVelocity.normalize(EurekaConfig.SERVER.linearStabilizeMaxAntiVelocity)

        idealVelocity.mul(ship.inertia.shipMass * (10 - EurekaConfig.SERVER.antiVelocityMassRelevance))
        forces.applyInvariantForce(idealVelocity)
    }
}
