package org.valkyrienskies.eureka

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object EurekaConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {

        @JsonSchema(description = "Movement power per engine heated fully")
        val enginePower: Float = 2000000f

        @JsonSchema(description = "Movement power per engine with minimal heat")
        val minEnginePower: Float = 700000f

        @JsonSchema(description = "The amount of heat a engine loses per tick")
        val engineHeatLoss = 0.01f

        @JsonSchema(description = "The amount of heat a gain per tick (when burning)")
        val engineHeatGain = 0.03f

        @JsonSchema(description = "Max speed of a ship without boosting")
        val maxCasualSpeed = 20f

        @JsonSchema(description = "The speed at which the ship stabilizes")
        var stabilizationSpeed = 10.0

        @JsonSchema(description = "The amount extra that each floater will make the ship float, per kg mass")
        var floaterBuoyantFactorPerKg = 50_000.0

        @JsonSchema(description = "The maximum amount extra each floater will multiply the buoyant force by, irrespective of mass")
        var maxFloaterBuoyantFactor = 1.0

        // The velocity any ship at least can move at.
        @JsonSchema(description = "The speed a ship with no engines can move at")
        var baseSpeed = 3.0

        // Sensitivity of the up/down impulse buttons.
        // TODO maybe should be moved to VS2 client-side config?
        @JsonSchema(description = "Vertical sensitivity up ascend/descend")
        var impulseElevationRate = 7

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

        // The strength used when trying to level the ship
        @JsonSchema(description = "How much torque a ship will apply to try and keep level")
        var stabilizationTorqueConstant = 15.0

        // Max anti-velocity used when trying to stop the ship
        @JsonSchema(description = "How fast a ship will stop. 1 = fast stop, 0 = slow stop")
        var linearStabilizeMaxAntiVelocity = 1.0

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
            "minecraft:portal"
        )

        @JsonSchema(description = "Whether the ship helm assembles diagonally connected blocks or not")
        val diagonals = true

        @JsonSchema(description = "Weight of ballast when lowest redstone power")
        val ballastWeight: Double = 10000.0

        @JsonSchema(description = "Weight of ballast when highest redstone power")
        val ballastNoWeight: Double = 1000.0

        @JsonSchema(description = "Whether or not disassembly is permitted")
        val allowDisassembly = true
    }
}
