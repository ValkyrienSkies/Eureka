package org.valkyrienskies.eureka

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object EurekaConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {

        @JsonSchema(description = "Movement power per engine when heated fully")
        val enginePowerLinear: Float = 500000f

        @JsonSchema(description = "Movement power per engine with minimal heat")
        val enginePowerLinearMin: Float = 10000f

        @JsonSchema(description = "Turning power per engine when heated fully")
        val enginePowerAngular = 1.0f

        @JsonSchema(description = "Turning power per engine when minimal heat")
        val enginePowerAngularMin = 0.0f

        @JsonSchema(description = "The amount of heat a engine loses per tick")
        val engineHeatLoss = 0.01f

        @JsonSchema(description = "The amount of heat a gain per tick (when burning)")
        val engineHeatGain = 0.03f

        @JsonSchema(description = "Increases heat gained at low heat level, and increased heat decreases when at high heat and not consuming fuel")
        val engineHeatChangeExponent = 0.1f

        @JsonSchema(description = "Pause fuel consumption and power when block is powered")
        val engineRedstoneBehaviorPause = false

        @JsonSchema(description = "Avoids consuming fuel when heat is 100%")
        val engineFuelSaving = false

        @JsonSchema(description = "Increasing this value will result in more items being able to converted to fuel")
        val engineMinCapacity = 2000

        @JsonSchema(description = "Fuel burn time multiplier")
        val engineFuelMultiplier = 2f

        @JsonSchema(description = "Extra engine power for when having multiple engines per engine")
        val engineBoost = 0.2

        @JsonSchema(description = "At what amount of engines the boost will start taking effect")
        val engineBoostOffset = 2.5

        @JsonSchema(description = "The final linear boost will be raised to the power of 2, and the result of the delta is multiple by this value")
        val engineBoostExponentialPower = 0.000001

        @JsonSchema(description = "Max speed of a ship without boosting")
        val maxCasualSpeed = 15.0

        @JsonSchema(description = "The speed at which the ship stabilizes")
        var stabilizationSpeed = 10.0

        @JsonSchema(description = "The amount extra that each floater will make the ship float, per kg mass")
        var floaterBuoyantFactorPerKg = 50_000.0

        @JsonSchema(description = "The maximum amount extra each floater will multiply the buoyant force by, irrespective of mass")
        var maxFloaterBuoyantFactor = 1.0

        @JsonSchema(description = "how much the mass decreases the speed.")
        var speedMassScale = 5.0

        // The velocity any ship at least can move at.
        @JsonSchema(description = "The speed a ship with no engines can move at")
        var baseSpeed = 3.0

        // Sensitivity of the up/down impulse buttons.
        // TODO maybe should be moved to VS2 client-side config?
        @JsonSchema(description = "Vertical sensitivity when ascending")
        var baseImpulseElevationRate = 2.0

        @JsonSchema(description = "Vertical sensitivity when descending")
        var baseImpulseDescendRate = 4.0

        @JsonSchema(description = "The max elevation speed boost gained by having extra extra balloons")
        var balloonElevationMaxSpeed = 5.5

        // Higher numbers make the ship accelerate to max speed faster
        @JsonSchema(description = "Ascend and descend acceleration")
        var elevationSnappiness = 1.0

        // Allow Eureka controlled ships to be affected by fluid drag
        @JsonSchema(description = "Allow Eureka controlled ships to be affected by fluid drag")
        var doFluidDrag = false

        // Do i need to explain? the mass 1 baloon gets to float
        @JsonSchema(description = "Amount of mass in kg a balloon can lift")
        var massPerBalloon = 5000.0

        // The amount of speed that the ship can move at when the left/right impulse button is held down.
        @JsonSchema(description = "The maximum linear velocity at any point on the ship caused by helm torque")
        var turnSpeed = 3.0

        @JsonSchema(description = "The maximum linear acceleration at any point on the ship caused by helm torque")
        var turnAcceleration = 10.0

        @JsonSchema(
            description = "The maximum distance from center of mass to one end of the ship considered by " +
                "the turn speed. At it's default of 16, it ensures that really large ships will turn at the same " +
                "speed as a ship with a center of mass only 16 blocks away from the farthest point in the ship. " +
                "That way, large ships do not turn painfully slowly"
        )
        var maxSizeForTurnSpeedPenalty = 16.0

        // The strength used when trying to level the ship
        @JsonSchema(description = "How much torque a ship will apply to try and keep level")
        var stabilizationTorqueConstant = 15.0

        // Max anti-velocity used when trying to stop the ship
        @JsonSchema(description = "How fast a ship will stop. 1 = fast stop, 0 = slow stop")
        var linearStabilizeMaxAntiVelocity = 1.0

        @JsonSchema(description = "How fast a ship will stop and accelerate.")
        var linearMassScaling = 0.0002

        // Must be positive. higher value will case slower acceleration and deceleration.
        @JsonSchema(description = "Base mass for linear acceleration in Kg.")
        var linearBaseMass = 50.0

        //when value is same as linearMaxMass, actual value will be 1/3. actual value will be close to linearMaxMass when 5 times over
        @JsonSchema(description = "Max smoothing value, will smooth out before reaching max value.")
        var linearMaxMass = 10000.0

        @JsonSchema(description = "Max unscaled speed in m/s.")
        var linearMaxSpeed = 15.0

        // Anti-velocity mass relevance when stopping the ship
        // Max 10.0 (means no mass irrelevance)
        @JsonSchema(description = "How much inertia affects Eureka ships. Max 10 = full inertia")
        var antiVelocityMassRelevance = 0.8

        // Chance that if side will pop, its this chance per side
        @JsonSchema(description = "Chance for popped balloons to pop adjacent balloons, per side")
        var popSideBalloonChance = 0.3

        // Blacklist of blocks that don't get added for ship building
        @JsonSchema(description = "Blacklist of blocks that don't get assembled")
        var blockBlacklist = setOf(
            "vs_eureka:ship_helm",
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:grass_path",
            "minecraft:stone",
            "minecraft:bedrock",
            "minecraft:sand",
            "minecraft:gravel",
            "minecraft:water",
            "minecraft:flowing_water",
            "minecraft:lava",
            "minecraft:flowing_lava",
            "minecraft:lily_pad",
            "minecraft:coarse_dirt",
            "minecraft:podzol",
            "minecraft:granite",
            "minecraft:diorite",
            "minecraft:andesite",
            "minecraft:deepslate",
            "minecraft:tuff",
            "minecraft:crimson_nylium",
            "minecraft:warped_nylium",
            "minecraft:red_sand",
            "minecraft:sandstone",
            "minecraft:end_stone",
            "minecraft:red_sandstone",
            "minecraft:blackstone",
            "minecraft:netherrack",
            "minecraft:soul_sand",
            "minecraft:soul_soil",
            "minecraft:grass",
            "minecraft:fern",
            "minecraft:dead_bush",
            "minecraft:seagrass",
            "minecraft:tall_seagrass",
            "minecraft:sea_pickle",
            "minecraft:kelp",
            "minecraft:bamboo",
            "minecraft:dandelion",
            "minecraft:poppy",
            "minecraft:blue_orchid",
            "minecraft:allium",
            "minecraft:azure_bluet",
            "minecraft:red_tulip",
            "minecraft:orange_tulip",
            "minecraft:white_tulip",
            "minecraft:pink_tulip",
            "minecraft:oxeye_daisy",
            "minecraft:cornflower",
            "minecraft:lily_of_the_valley",
            "minecraft:brown_mushroom",
            "minecraft:red_mushroom",
            "minecraft:crimson_fungus",
            "minecraft:warped_fungus",
            "minecraft:crimson_roots",
            "minecraft:warped_roots",
            "minecraft:nether_sprouts",
            "minecraft:weeping_vines",
            "minecraft:twisting_vines",
            "minecraft:chorus_plant",
            "minecraft:chorus_flower",
            "minecraft:snow",
            "minecraft:snow_block",
            "minecraft:cactus",
            "minecraft:vine",
            "minecraft:sunflower",
            "minecraft:lilac",
            "minecraft:rose_bush",
            "minecraft:peony",
            "minecraft:tall_grass",
            "minecraft:large_fern",
            "minecraft:air",
            "minecraft:ice",
            "minecraft:packed_ice",
            "minecraft:blue_ice",
            "minecraft:portal",
            "minecraft:bedrock",
            "minecraft:end_portal_frame",
            "minecraft:end_portal",
            "minecraft:end_gateway",
            "minecraft:portal",
            "minecraft:oak_sapling",
            "minecraft:spruce_sapling",
            "minecraft:birch_sapling",
            "minecraft:jungle_sapling",
            "minecraft:acacia_sapling",
            "minecraft:dark_oak_sapling",
            "minecraft:oak_leaves",
            "minecraft:spruce_leaves",
            "minecraft:birch_leaves",
            "minecraft:jungle_leaves",
            "minecraft:acacia_leaves",
            "minecraft:dark_oak_leaves"
        )

        @JsonSchema(description = "Whether the ship helm assembles diagonally connected blocks or not")
        val diagonals = true

        @JsonSchema(description = "Weight of ballast when lowest redstone power")
        val ballastWeight: Double = 10000.0

        @JsonSchema(description = "Weight of ballast when highest redstone power")
        val ballastNoWeight: Double = 1000.0

        @JsonSchema(description = "Whether or not disassembly is permitted")
        val allowDisassembly = true

        @JsonSchema(description = "Maximum number of blocks allowed in a ship. Set to 0 for no limit")
        val maxShipBlocks = 32 * 32 * 32
    }
}
