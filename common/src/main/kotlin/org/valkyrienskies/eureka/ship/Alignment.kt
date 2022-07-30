package org.valkyrienskies.eureka.ship

import net.minecraft.core.Direction
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.Ship
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.floor

// TODO illegal usage of [Ship]
fun alignShip(physShip: PhysShip, forces: ForcesApplier, ship: Ship): Boolean {

    // region Align Rotation
    val worldFront = Vector3d(0.0, 0.0, 1.0)
    val shipFront = Vector3d(worldFront)

    physShip.rotation.transform(shipFront)

    val angle = shipFront.angle(worldFront)

    // value between 0 and 3 (inclusive) to horizontal direction
    val closestDirection = Direction.from2DDataValue(floor((angle / (Math.PI / 2.0)) + 0.5).toInt())

    // Align to closest direction
    val alignFront = closestDirection.normal.toJOMLD()
    val angleAlign = shipFront.angle(alignFront)

    val idealAlignAccel =
        shipFront.cross(alignFront, alignFront).normalize().mul(angleAlign, alignFront)

    idealAlignAccel.sub(physShip.omega)

    val alignTorque = physShip.rotation.transform(
        physShip.inertia.momentOfInertiaTensor.transform(
            physShip.rotation.transformInverse(
                idealAlignAccel,
                idealAlignAccel
            )
        )
    )

    alignTorque.mul(EurekaConfig.SERVER.stabilizationTorqueConstant * 10.0)
    forces.applyInvariantTorque(alignTorque)
    // endregion
    // region Align Position
    // Any position in the middle of a block
    val shipPos = Vector3d(physShip.position)
    ship.worldToShip.transformPosition(shipPos)
    shipPos.floor().add(0.5, 0.5, 0.5)

    val midBlockPos = ship.shipToWorld.transformPosition(shipPos)
    val flooredPos = midBlockPos.floor(Vector3d())
    val worldCenterBlock = flooredPos.add(0.5, 0.5, 0.5)
    val diff = worldCenterBlock.sub(midBlockPos)

    val linearForces = diff.sub(physShip.velocity, midBlockPos)
    linearForces.mul(physShip.inertia.shipMass * 10)

    forces.applyInvariantForce(linearForces)
    // endregion

    return angleAlign < 0.01 && diff.lengthSquared() < 0.01
}
