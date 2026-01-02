package org.valkyrienskies.eureka

import net.minecraftforge.common.ForgeConfigSpec
import net.minecraftforge.fml.config.ModConfig
import org.jetbrains.annotations.ApiStatus
import org.valkyrienskies.core.internal.config.ConfigEntry
import org.valkyrienskies.mod.api.config.VSConfigApi
import org.valkyrienskies.mod.api.config.VSConfigApi.update
import org.valkyrienskies.mod.common.config.ConfigType
import org.valkyrienskies.mod.common.hooks.VSGameEvents.ConfigUpdateEntry
import java.util.HashMap

object EurekaConfig {

    @JvmStatic
    val forgeConfigValuesMap: HashMap<String, ForgeConfigSpec.ConfigValue<*>> = HashMap()

    private val configValueConsumer = { name: String, value: ForgeConfigSpec.ConfigValue<*> ->
        forgeConfigValuesMap[name] = value
    }

    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    private val eurekaConfig = VSConfigApi.buildVSConfigModel(SERVER)
    val EUREKA_SPEC: ForgeConfigSpec = VSConfigApi.buildForgeConfigSpec(
        configCategory = eurekaConfig.root,
        builder = ForgeConfigSpec.Builder(),
        forgeConfigValueConsumer = configValueConsumer
    ).build()

    class Client

    class Server {

        @ConfigEntry(description = "Movement power per engine when heated fully")
        var enginePowerLinear: Float = 500000f

        @ConfigEntry(description = "Movement power per engine with minimal heat")
        var enginePowerLinearMin: Float = 10000f

        @ConfigEntry(description = "Turning power per engine when heated fully")
        var enginePowerAngular = 1.0f

        @ConfigEntry(description = "Turning power per engine when minimal heat")
        var enginePowerAngularMin = 0.0f

        @ConfigEntry(description = "The amount of heat a engine loses per tick")
        var engineHeatLoss = 0.01f

        @ConfigEntry(description = "The amount of heat a gain per tick (when burning)")
        var engineHeatGain = 0.03f

        @ConfigEntry(description = "Increases heat gained at low heat level, and increased heat decreases when at high heat and not consuming fuel")
        var engineHeatChangeExponent = 0.1f

        @ConfigEntry(description = "Pause fuel consumption and power when block is powered")
        var engineRedstoneBehaviorPause = false

        @ConfigEntry(description = "Number of Balloons a single engine can power. 0 disables the feature")
        var maxBalloonsPerEngine = 0

        @ConfigEntry(description = "Avoids consuming fuel when heat is 100%")
        var engineFuelSaving = false

        @ConfigEntry(description = "Increasing this varue will result in more items being able to converted to fuel")
        var engineMinCapacity = 2000

        @ConfigEntry(description = "Fuel burn time multiplier")
        var engineFuelMultiplier = 2f

        @ConfigEntry(description = "Extra engine power for when having multiple engines per engine")
        var engineBoost = 0.2

        @ConfigEntry(description = "At what amount of engines the boost will start taking effect")
        var engineBoostOffset = 2.5

        @ConfigEntry(description = "The final linear boost will be raised to the power of 2, and the result of the delta is multiple by this value")
        var engineBoostExponentialPower = 0.000001

        @ConfigEntry(description = "Max speed of a ship with engines (actual max speed varies with engines and mass.)")
        var maxSpeedFromEngines = 12.0

        @ConfigEntry(description = "Max reverse speed of a ship with engines")
        var maxReverseSpeedFromEngines = 5.0

        @ConfigEntry(description = "The speed at which the ship stabilizes")
        var stabilizationSpeed = 10.0

        @ConfigEntry(description = "How how much force is Kg each floater supports")
        var floaterBuoyantLift = 2_000.0

        @ConfigEntry(description = "The maximum amount extra each floater will multiply the buoyant force by, irrespective of mass")
        var maxFloaterBuoyantFactor = 0.1

        @ConfigEntry(description = "how much the mass decreases the speed.")
        var speedMassScale = 1.0

        // Sensitivity of the up/down impulse buttons.
        // TODO maybe should be moved to VS2 client-side config?
        @ConfigEntry(description = "Vertical sensitivity when ascending")
        var baseImpulseElevationRate = 2.0

        @ConfigEntry(description = "Vertical sensitivity when descending")
        var baseImpulseDescendRate = 4.0

        @ConfigEntry(description = "The max elevation speed boost gained by having extra extra balloons")
        var balloonElevationMaxSpeed = 5.5

        // Higher numbers make the ship accelerate to max speed faster
        @ConfigEntry(description = "Ascend and descend acceleration")
        var elevationSnappiness = 1.0

        @ConfigEntry(description = "Allows ships without a helm (passive ships) to use floaters and balloons")
        var allowFloatersAndBalloonsOnNonEurekaShips = true

        @ConfigEntry(description = "The height where balloons starts to loose effectiveness on ships without a helm")
        var passiveBalloonMinHeight = 64.0

        @ConfigEntry(description = "The height where balloons effectiveness is zero on ships without a helm")
        var passiveBalloonMaxHeight = 400.0

        // Allow Eureka controlled ships to be affected by fluid drag
        @ConfigEntry(description = "Allow Eureka controlled ships to be affected by fluid drag")
        var doFluidDrag = false

        // Do i need to explain? the mass 1 baloon gets to float
        @ConfigEntry(description = "Amount of mass in kg a balloon can lift")
        var massPerBalloon = 5000.0

        // The amount of speed that the ship can move at when the left/right impulse button is held down.
        @ConfigEntry(description = "The maximum linear velocity at any point on the ship caused by helm torque")
        var turnSpeed = 3.0

        @ConfigEntry(description = "The maximum linear acceleration at any point on the ship caused by helm torque")
        var turnAcceleration = 10.0

        @ConfigEntry(
            description = "The maximum distance from center of mass to one end of the ship considered by " +
                "the turn speed. At it's default of 16, it ensures that really large ships will turn at the same " +
                "speed as a ship with a center of mass only 16 blocks away from the farthest point in the ship. " +
                "That way, large ships do not turn painfully slowly"
        )
        var maxSizeForTurnSpeedPenalty = 16.0

        // The strength used when trying to level the ship
        @ConfigEntry(description = "How much torque a ship will apply to try and keep level")
        var stabilizationTorqueConstant = 15.0

        // Max anti-velocity used when trying to stop the ship
        @ConfigEntry(description = "How fast a ship will stop. 1 = fast stop, 0 = slow stop")
        var linearStabilizeMaxAntiVelocity = 1.0

        // Instability scaled with mass and squared speed
        @ConfigEntry(description = "Stronger stabilization with higher mass, less at higher speeds.")
        var scaledInstability = 70.0

        // Unscaled linear instability cased by speed
        @ConfigEntry(description = "Less stabilization at higher speed.")
        var unscaledInstability = 0.1

        @ConfigEntry(description = "How fast a ship will stop and accelerate.")
        var linearMassScaling = 0.0002

        // Must be positive. higher value will case slower acceleration and deceleration.
        @ConfigEntry(description = "Base mass for linear acceleration in Kg.")
        var linearBaseMass = 50.0

        // when value is same as linearMaxMass. actual value will be close to linearMaxMass when 5 times over
        @ConfigEntry(description = "Max mass for the linear stabilisation, will smooth out before reaching max value.")
        var linearMaxMass = 10000.0

        @ConfigEntry(description = "Max unscaled speed in m/s without engines.")
        var linearBaseSpeed = 3.0

        // Anti-velocity mass relevance when stopping the ship
        // Max 10.0 (means no mass irrelevance)
        @ConfigEntry(description = "How much inertia affects Eureka ships. Max 10 = full inertia")
        var antiVelocityMassRelevance = 0.8

        // Chance that if side will pop, its this chance per side
        @ConfigEntry(description = "Chance for popped balloons to pop adjacent balloons, per side")
        var popSideBalloonChance = 0.3

        @ConfigEntry(description = "Whether the ship helm assembles diagonally connected blocks or not")
        var diagonals = true

        @ConfigEntry(description = "Weight of ballast when lowest redstone power")
        var ballastWeight: Double = 10000.0

        @ConfigEntry(description = "Weight of ballast when highest redstone power")
        var ballastNoWeight: Double = 1000.0

        @ConfigEntry(description = "Whether or not disassembly is permitted")
        var allowDisassembly = true

        @ConfigEntry(description = "Maximum number of blocks allowed in a ship. Set to 0 for no limit")
        var maxShipBlocks = 32 * 32 * 32
    }
    @ApiStatus.Internal
    fun update(config: ModConfig) {
        val updatedEntries = mutableSetOf<ConfigUpdateEntry>()
        eurekaConfig.update(config, ConfigType.CORE_SERVER, updatedEntries)
    }
}
