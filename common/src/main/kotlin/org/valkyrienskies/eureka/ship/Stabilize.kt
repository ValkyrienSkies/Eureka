package org.valkyrienskies.eureka.ship

import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.eureka.EurekaConfig

fun stabilize(ship: PhysShip, forces: ForcesApplier, linear: Boolean, yaw: Boolean) {
    val shipUp = Vector3d(0.0, 1.0, 0.0)
    val worldUp = Vector3d(0.0, 1.0, 0.0)
    ship.rotation.transform(shipUp)

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
        ship.omega.x(),
        if (!yaw) 0.0 else ship.omega.y(),
        ship.omega.z()
    )

    val stabilizationTorque = ship.rotation.transform(
        ship.inertia.momentOfInertiaTensor.transform(
            ship.rotation.transformInverse(
                idealAngularAcceleration,
                idealAngularAcceleration
            )
        )
    )

    stabilizationTorque.mul(EurekaConfig.SERVER.stabilizationTorqueConstant)
    forces.applyInvariantTorque(stabilizationTorque)

    if (linear) {
        val idealVelocity = Vector3d(ship.velocity).negate()
        idealVelocity.mul(ship.inertia.shipMass * 10)
        forces.applyInvariantForce(idealVelocity)
    }
}
