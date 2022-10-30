package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.Direction
import org.joml.AxisAngle4d
import org.joml.Math.clamp
import org.joml.Math.cos
import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.ForcesApplier
import org.valkyrienskies.core.api.ServerShip
import org.valkyrienskies.core.api.ServerShipUser
import org.valkyrienskies.core.api.ShipForcesInducer
import org.valkyrienskies.core.api.Ticked
import org.valkyrienskies.core.api.getAttachment
import org.valkyrienskies.core.api.saveAttachment
import org.valkyrienskies.core.api.shipValue
import org.valkyrienskies.core.game.ships.PhysShip
import org.valkyrienskies.core.pipelines.SegmentUtils
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

class EurekaShipControl(var elevationTarget: Double) : ShipForcesInducer, ServerShipUser, Ticked {

    @JsonIgnore
    override var ship: ServerShip? = null
    val controllingPlayer by shipValue<SeatedControllingPlayer>()

    private var extraForce = 0.0
    var aligning = false
    private var cruiseSpeed = Double.NaN
    private var physConsumption = 0f
    private val anchored get() = anchorsActive > 0
    private val anchorSpeed = EurekaConfig.SERVER.anchorSpeed
    private var wasAnchored = false
    private var anchorTargetPos = Vector3d()
    private var anchorTargetRot = Quaterniond()
    private val elevationPower get() = balloons.toDouble()

    private var angleUntilAligned = 0.0
    private var alignTarget = 0
    val canDisassemble get() = angleUntilAligned < DISASSEMBLE_THRESHOLD
    val aligningTo get() = Direction.from2DDataValue(alignTarget)
    var consumed = 0f
        private set

    override fun applyForces(forcesApplier: ForcesApplier, physShip: PhysShip) {

        if (helms < 1) {
            return
        }

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val omega = SegmentUtils.getOmega(physShip.poseVel, segment, Vector3d())
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())
        val pos = physShip.poseVel.pos

        val buoyantFactorPerFloater = min(
            EurekaConfig.SERVER.floaterBuoyantFactorPerKg / 15 / mass,
            EurekaConfig.SERVER.maxFloaterBuoyantFactor
        )

        physShip.buoyantFactor = 1.0 + floaters * buoyantFactorPerFloater
        // Revisiting eureka control code.
        // [x] Move torque stabilization code
        // [x] Move linear stabilization code
        // [x] Revisit player controlled torque
        // [x] Revisit player controlled linear force
        // [x] Anchor freezing
        // [x] Rewrite Alignment code
        // [x] Revisit Elevation code
        // [x] Balloon limiter
        // [ ] Add Cruise code
        // [ ] Rotation based of shipsize
        // [x] Engine consumption
        // [ ] Fix elevation sensititvity

        // region Aligning

        val invRotation = physShip.poseVel.rot.invert(Quaterniond())
        val invRotationAxisAngle = AxisAngle4d(invRotation)
        // Floor makes a number 0 to 3, wich corresponds to direction
        alignTarget = floor((invRotationAxisAngle.angle / (PI * 0.5)) + 4.5).toInt() % 4
        angleUntilAligned = (alignTarget.toDouble() * (0.5 * Math.PI)) - invRotationAxisAngle.angle
        if (aligning && abs(angleUntilAligned) > ALIGN_THRESHOLD) {
            if (angleUntilAligned < 0.3 && angleUntilAligned > 0.0) angleUntilAligned = 0.3
            if (angleUntilAligned > -0.3 && angleUntilAligned < 0.0) angleUntilAligned = -0.3

            val idealOmega = Vector3d(invRotationAxisAngle.x, invRotationAxisAngle.y, invRotationAxisAngle.z)
                .mul(-angleUntilAligned)
                .mul(EurekaConfig.SERVER.stabilizationSpeed)

            val idealTorque = moiTensor.transform(idealOmega)

            forcesApplier.applyInvariantTorque(idealTorque)
        }
        // endregion

        stabilize(
            physShip,
            omega,
            vel,
            segment,
            forcesApplier,
            controllingPlayer == null && !aligning,
            controllingPlayer == null
        )

        controllingPlayer?.let { player ->
            // region Player controlled rotation
            var rotationVector = Vector3d(
                0.0,
                if (player.leftImpulse != 0.0f)
                    (player.leftImpulse.toDouble() * EurekaConfig.SERVER.turnSpeed)
                else
                    -omega.y() * EurekaConfig.SERVER.turnSpeed,
                0.0
            )
            // rotationVector.add(player.seatInDirection.normal.toJOMLD().mul(player.leftImpulse.toDouble() * EurekaConfig.SERVER.turnSpeed))

            rotationVector.sub(0.0, omega.y(), 0.0)

            SegmentUtils.transformDirectionWithScale(
                physShip.poseVel,
                segment,
                moiTensor.transform(
                    SegmentUtils.invTransformDirectionWithScale(
                        physShip.poseVel,
                        segment,
                        rotationVector,
                        rotationVector
                    )
                ),
                rotationVector
            )

            forcesApplier.applyInvariantTorque(rotationVector)
            // endregion

            // region Player controlled banking
            rotationVector = player.seatInDirection.normal.toJOMLD()

            physShip.poseVel.transformDirection(rotationVector)

            rotationVector.y = 0.0

            rotationVector.mul(player.leftImpulse.toDouble() * EurekaConfig.SERVER.turnSpeed * -1.5)

            SegmentUtils.transformDirectionWithScale(
                physShip.poseVel,
                segment,
                moiTensor.transform(
                    SegmentUtils.invTransformDirectionWithScale(
                        physShip.poseVel,
                        segment,
                        rotationVector,
                        rotationVector
                    )
                ),
                rotationVector
            )

            forcesApplier.applyInvariantTorque(rotationVector)
            // endregion

            // region Player controlled forward and backward thrust
            val forwardVector = player.seatInDirection.normal.toJOMLD()
            SegmentUtils.transformDirectionWithoutScale(
                physShip.poseVel,
                segment,
                forwardVector,
                forwardVector
            )
            forwardVector.mul(player.forwardImpulse.toDouble())
            val idealForwardVel = Vector3d(forwardVector)
            idealForwardVel.mul(EurekaConfig.SERVER.baseSpeed)
            val idealCasualForwardVelInc = Vector3d(forwardVector).mul(EurekaConfig.SERVER.maxCasualSpeed.toDouble())
            idealCasualForwardVelInc.sub(vel.x(), 0.0, vel.z())
            val forwardVelInc = idealForwardVel.sub(vel.x(), 0.0, vel.z())
            forwardVelInc.mul(mass * 10)
            idealCasualForwardVelInc.mul(mass * 10)

            val extraForceNeeded = idealCasualForwardVelInc.min(forwardVelInc)

            if (extraForce != 0.0) {
                val usage = min(extraForceNeeded.length() / extraForce, 1.0)
                physConsumption += usage.toFloat()
                forwardVelInc.add(forwardVector.mul(extraForce).mul(usage))
            }

            forcesApplier.applyInvariantForce(forwardVelInc)
            // endregion

            // Player controlled elevation
            if (player.upImpulse != 0.0f && balloons > 0)
                elevationTarget =
                    pos.y() + (
                            player.upImpulse * EurekaConfig.SERVER.impulseElevationRate * min(
                                elevationPower * 0.2,
                                1.5
                            )
                            )
        }

        // region Elevation
        if (elevationTarget.isFinite() && balloons > 0) {
            val massPenalty =
                min((elevationPower / (mass * BALLOON_PER_MASS)) - 1.0, elevationPower) * NEUTRAL_FLOAT
            val limit = (NEUTRAL_LIMIT + massPenalty)
            var stable = true

            val elevationPower = if (pos.y() > limit) {
                if ((pos.y() - limit) > 20.0) {
                    stable = false
                    0.0
                } else {
                    val mod = 1 + cos(((pos.y() - limit) / 10.0) * (Math.PI / 2) + (Math.PI / 2))
                    if ((pos.y() - limit) > 10.0) {
                        stable = false
                        (1 - mod) * 0.1 + 0.1
                    } else elevationPower * mod
                }
            } else elevationPower

            val diff = (elevationTarget - pos.y())
                .let { if (abs(it) < 0.05) 0.0 else it }

            val penalisedVel = if (elevationPower < 0.1) 0.0 else
                (MAX_RISE_VEL * elevationPower)

            val shipRiseVelo = vel.y()
            val idealRiseVelo = clamp(-MAX_RISE_VEL, penalisedVel, diff)
            val impulse = idealRiseVelo - shipRiseVelo

            if (idealRiseVelo > 0.1 || stable)
                forcesApplier.applyInvariantForce(Vector3d(0.0, (impulse + if (stable) 1 else 0) * mass * 10, 0.0))
        }
        // endregion

        // region Anchor
        if (wasAnchored != anchored) {
            anchorTargetPos = physShip.poseVel.pos as Vector3d
            anchorTargetRot = physShip.poseVel.rot as Quaterniond
            wasAnchored = anchored
        }
        if (anchored && anchorTargetPos.isFinite) { // TODO: Same thing but with rotation; rotate ship to anchor point
            val x1 = anchorTargetPos.x()
            val z1 = anchorTargetPos.z()
            val x2 = physShip.poseVel.pos.x()
            val z2 = physShip.poseVel.pos.z()
            val targetVel = Vector3d(x1 - x2, 0.0, z1 - z2)
            val len = targetVel.length()
            targetVel.mul(clamp(0.0, anchorSpeed, len * 10.0))
            targetVel.mul(physShip.inertia.shipMass)
            forcesApplier.applyInvariantForce(targetVel)

            val invRotation = physShip.poseVel.rot.invert(Quaterniond())
            val invRotationAxisAngle = AxisAngle4d(invRotation)

            val alignTarget = (anchorTargetRot.angle() / (0.5 * Math.PI))
            val angleUntilAligned = abs((alignTarget * (0.5 * Math.PI)) - invRotationAxisAngle.angle)
            val idealOmega = Vector3d(invRotationAxisAngle.x, invRotationAxisAngle.y, invRotationAxisAngle.z)
                .mul(angleUntilAligned)
                .mul(EurekaConfig.SERVER.stabilizationSpeed)

            val idealTorque = moiTensor.transform(idealOmega)

            forcesApplier.applyInvariantTorque(idealTorque)
        }
        // endregion

        // Drag
        // forcesApplier.applyInvariantForce(Vector3d(vel.y()).mul(-mass))
    }

    var power = 0.0
    var anchors = 0 // Amount of anchors
        set(v) {
            field = v; deleteIfEmpty()
        }

    var anchorsActive = 0 // Anchors that are active
    var balloons = 0 // Amount of balloons
        set(v) {
            field = v; deleteIfEmpty()
        }

    var helms = 0 // Amount of helms
        set(v) {
            field = v; deleteIfEmpty()
        }

    var floaters = 0 // Amount of floaters * 15
        set(v) {
            field = v; deleteIfEmpty()
        }

    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption * /* should be phyics ticks based*/ 0.1f
        physConsumption = 0.0f
    }

    fun deleteIfEmpty() {
        if (helms == 0 && floaters == 0 && anchors == 0 && balloons == 0) {
            ship?.saveAttachment<EurekaShipControl>(null)
        }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): EurekaShipControl {
            return ship.getAttachment<EurekaShipControl>()
                ?: EurekaShipControl(ship.shipTransform.shipPositionInWorldCoordinates.y()).also {
                    ship.saveAttachment(
                        it
                    )
                }
        }

        private const val ALIGN_THRESHOLD = 0.01
        private const val DISASSEMBLE_THRESHOLD = 0.02
        private const val MAX_RISE_VEL = 3.0
        private val BALLOON_PER_MASS get() = 1 / EurekaConfig.SERVER.massPerBalloon
        private val NEUTRAL_FLOAT get() = EurekaConfig.SERVER.neutralLimit
        private val NEUTRAL_LIMIT get() = NEUTRAL_FLOAT - 10
    }
}
