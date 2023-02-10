package org.valkyrienskies.eureka.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import net.minecraft.core.Direction
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.entity.player.Player
import org.joml.AxisAngle4d
import org.joml.Quaterniond
import org.joml.Vector3d
import org.joml.Vector3dc
import org.valkyrienskies.core.api.VSBeta
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.api.shipValue
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import org.valkyrienskies.eureka.EurekaConfig
import org.valkyrienskies.mod.api.SeatedControllingPlayer
import org.valkyrienskies.mod.common.util.toJOMLD
import kotlin.math.*

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
class EurekaShipControl : ShipForcesInducer, ServerShipUser, Ticked {

    @JsonIgnore
    override var ship: ServerShip? = null

    @delegate:JsonIgnore
    private val controllingPlayer by shipValue<SeatedControllingPlayer>()

    private var extraForce = 0.0
    var aligning = false
    var disassembling = false // Disassembling also affects position
    private var physConsumption = 0f
    private val anchored get() = anchorsActive > 0

    private var angleUntilAligned = 0.0
    private var positionUntilAligned = Vector3d()
    private var alignTarget = 0
    val canDisassemble
        get() = ship != null &&
                disassembling &&
                abs(angleUntilAligned) < DISASSEMBLE_THRESHOLD &&
                positionUntilAligned.distanceSquared(this.ship!!.transform.positionInWorld) < 4.0
    val aligningTo: Direction get() = Direction.from2DDataValue(alignTarget)
    var consumed = 0f
        private set

    private var wasCruisePressed = false
    @JsonProperty("cruise")
    var isCruising = false
    private var controlData: ControlData? = null

    @JsonIgnore
    var seatedPlayer: Player? = null

    private data class ControlData(
        val seatInDirection: Direction,
        var forwardImpulse: Float = 0.0f,
        var leftImpulse: Float = 0.0f,
        var upImpulse: Float = 0.0f,
        var sprintOn: Boolean = false
    ) {
        companion object {
            fun create(player: SeatedControllingPlayer): ControlData {
                return ControlData(
                    player.seatInDirection,
                    player.forwardImpulse,
                    player.leftImpulse,
                    player.upImpulse,
                    player.sprintOn
                )
            }
        }
    }

    @OptIn(VSBeta::class)
    override fun applyForces(physShip: PhysShip) {
        if (helms < 1) {
            // Enable fluid drag if all the helms have been destroyed
            physShip.doFluidDrag = true
            return
        }
        // Disable fluid drag when helms are present, because it makes ships hard to drive
        physShip.doFluidDrag = EurekaConfig.SERVER.doFluidDrag

        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val moiTensor = physShip.inertia.momentOfInertiaTensor
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val omega: Vector3dc = SegmentUtils.getOmega(physShip.poseVel, segment, Vector3d())
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())
        val ship = ship ?: return


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
        // Floor makes a number 0 to 3, which corresponds to direction
        alignTarget = floor((invRotationAxisAngle.angle / (PI * 0.5)) + 4.5).toInt() % 4
        angleUntilAligned = (alignTarget.toDouble() * (0.5 * Math.PI)) - invRotationAxisAngle.angle
        if (disassembling) {
            val pos = ship.transform.positionInWorld
            positionUntilAligned = pos.floor(Vector3d())
            val direction = pos.sub(positionUntilAligned, Vector3d())
            physShip.applyInvariantForce(direction)
        }
        if ((aligning) && abs(angleUntilAligned) > ALIGN_THRESHOLD) {
            if (angleUntilAligned < 0.3 && angleUntilAligned > 0.0) angleUntilAligned = 0.3
            if (angleUntilAligned > -0.3 && angleUntilAligned < 0.0) angleUntilAligned = -0.3

            val idealOmega = Vector3d(invRotationAxisAngle.x, invRotationAxisAngle.y, invRotationAxisAngle.z)
                .mul(-angleUntilAligned)
                .mul(EurekaConfig.SERVER.stabilizationSpeed)

            val idealTorque = moiTensor.transform(idealOmega)

            physShip.applyInvariantTorque(idealTorque)
        }
        // endregion

        stabilize(
            physShip,
            omega,
            vel,
            segment,
            physShip,
            controllingPlayer == null && !aligning,
            controllingPlayer == null
        )

        var idealUpwardVel = Vector3d(0.0, 0.0, 0.0)

        val player = controllingPlayer

        if (player != null) {
            val currentControlData = ControlData.create(player)

            // If the player is currently controlling the ship
            if (!wasCruisePressed && player.cruise) {
                // the player pressed the cruise button
                isCruising = !isCruising
                showCruiseStatus()
            } else if (!player.cruise
                && isCruising
                && (player.leftImpulse != 0.0f || player.sprintOn || player.upImpulse != 0.0f || player.forwardImpulse != 0.0f)
                && currentControlData != controlData
            ) {
                // The player pressed another button
                isCruising = false
                showCruiseStatus()
            }

            if (!isCruising) {
                // only take the latest control data if the player is not cruising
                controlData = currentControlData
            }

            wasCruisePressed = player.cruise
        } else if (!isCruising) {
            // If the player isn't controlling the ship, and not cruising, reset the control data
            controlData = null
        }

        controlData?.let { control ->
            // region Player controlled rotation
            val transform = physShip.transform
            val aabb = ship.worldAABB
            val center = transform.positionInWorld
            val stw = transform.shipToWorld
            val wts = transform.worldToShip

            val largestDistance = run {
                var dist = center.distance(aabb.minX(), center.y(), aabb.minZ())
                dist = max(dist, center.distance(aabb.minX(), center.y(), aabb.maxZ()))
                dist = max(dist, center.distance(aabb.maxX(), center.y(), aabb.minZ()))
                dist = max(dist, center.distance(aabb.maxX(), center.y(), aabb.maxZ()))

                dist
            }.coerceAtLeast(0.5)

            val maxLinearAcceleration = EurekaConfig.SERVER.turnAcceleration
            val maxLinearSpeed = EurekaConfig.SERVER.turnSpeed

            // acceleration = alpha * r
            // therefore: maxAlpha = maxAcceleration / r
            val maxOmegaY = maxLinearSpeed / largestDistance
            val maxAlphaY = maxLinearAcceleration / largestDistance

            val isBelowMaxTurnSpeed = abs(omega.y()) < maxOmegaY

            val normalizedAlphaYMultiplier =
                if (isBelowMaxTurnSpeed && control.leftImpulse != 0.0f) control.leftImpulse.toDouble()
                else -omega.y().coerceIn(-1.0, 1.0)

            val idealAlphaY = normalizedAlphaYMultiplier * maxAlphaY

            val alpha = Vector3d(0.0, idealAlphaY, 0.0)
            val angularImpulse =
                stw.transformDirection(moiTensor.transform(wts.transformDirection(Vector3d(alpha))))

            val torque = Vector3d(angularImpulse)
            physShip.applyInvariantTorque(torque)
            // endregion

            // region Player controlled banking
            val rotationVector = control.seatInDirection.normal.toJOMLD()

            physShip.poseVel.transformDirection(rotationVector)

            rotationVector.y = 0.0

            rotationVector.mul(idealAlphaY * -1.5)

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

            physShip.applyInvariantTorque(rotationVector)
            // endregion

            // region Player controlled forward and backward thrust
            val forwardVector = control.seatInDirection.normal.toJOMLD()
            SegmentUtils.transformDirectionWithoutScale(
                physShip.poseVel,
                segment,
                forwardVector,
                forwardVector
            )
            forwardVector.y *= 0.1 // Reduce vertical thrust
            forwardVector.normalize()

            forwardVector.mul(control.forwardImpulse.toDouble())

            val playerUpDirection = physShip.poseVel.transformDirection(Vector3d(0.0, 1.0, 0.0))
            val velOrthogonalToPlayerUp =
                vel.sub(playerUpDirection.mul(playerUpDirection.dot(vel), Vector3d()), Vector3d())

            // This is the speed that the ship is always allowed to go out, without engines
            val baseForwardVel = Vector3d(forwardVector).mul(EurekaConfig.SERVER.baseSpeed)
            val baseForwardForce = Vector3d(baseForwardVel).sub(velOrthogonalToPlayerUp).mul(mass * 10)

            // This is the maximum speed we want to go in any scenario (when not sprinting)
            val idealForwardVel = Vector3d(forwardVector).mul(EurekaConfig.SERVER.maxCasualSpeed.toDouble())
            val idealForwardForce = Vector3d(idealForwardVel).sub(velOrthogonalToPlayerUp).mul(mass * 10)

            val extraForceNeeded = Vector3d(idealForwardForce).sub(baseForwardForce)
            val actualExtraForce = Vector3d(baseForwardForce)

            if (extraForce != 0.0) {
                actualExtraForce.fma(min(extraForce / extraForceNeeded.length(), 1.0), extraForceNeeded)
            }

            physShip.applyInvariantForce(actualExtraForce)
            // endregion

            // Player controlled elevation
            if (control.upImpulse != 0.0f) {
                idealUpwardVel = Vector3d(0.0, 1.0, 0.0)
                    .mul(control.upImpulse.toDouble())
                    .mul(EurekaConfig.SERVER.impulseElevationRate.toDouble())
            }
        }

        // region Elevation
        // Higher numbers make the ship accelerate to max speed faster
        val elevationSnappiness = 10.0
        val idealUpwardForce = Vector3d(
            0.0,
            idealUpwardVel.y() - vel.y() - (GRAVITY / elevationSnappiness),
            0.0
        ).mul(mass * elevationSnappiness)

        val balloonForceProvided = balloons * forcePerBalloon

        val actualUpwardForce = Vector3d(0.0, min(balloonForceProvided, max(idealUpwardForce.y(), 0.0)), 0.0)
        physShip.applyInvariantForce(actualUpwardForce)
        // endregion

        // region Anchor
        physShip.isStatic = anchored
        // endregion

        // Add drag to the y-component
        physShip.applyInvariantForce(Vector3d(vel.y()).mul(-mass))
    }

    private fun showCruiseStatus() {
        val cruiseKey = if (isCruising) "hud.vs_eureka.start_cruising" else "hud.vs_eureka.stop_cruising"
        seatedPlayer?.displayClientMessage(TranslatableComponent(cruiseKey), true)
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

    private fun deleteIfEmpty() {
        if (helms == 0 && floaters == 0 && anchors == 0 && balloons == 0) {
            ship?.saveAttachment<EurekaShipControl>(null)
        }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): EurekaShipControl {
            return ship.getAttachment<EurekaShipControl>()
                ?: EurekaShipControl().also { ship.saveAttachment(it) }
        }

        private const val ALIGN_THRESHOLD = 0.01
        private const val DISASSEMBLE_THRESHOLD = 0.02
        private val forcePerBalloon get() = EurekaConfig.SERVER.massPerBalloon * -GRAVITY

        private const val GRAVITY = -10.0
    }
}
