package org.valkyrienskies.eureka

object EurekaConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client

    class Server {
        // The velocity any ship at least can move at.
        var baseSpeed = 3.0

        // Sensitivity of the up/down impulse buttons.
        // TODO maybe should be moved to VS2 client-side config?
        var impulseAlleviationRate = 0.5

        // If a ship with weight 0 and 0 balloons would exist in the world, it would have this max attitude.
        var neutralLimit = 80.0

        // Do i need to explain? the mass 1 baloon gets to float
        var massPerBalloon = 5000.0

        // The amount of speed that the ship can move at when the left/right impulse button is held down.
        var turnSpeed = 3.0

        // The strength used when trying to level the ship
        var stabilizationTorqueConstant = 15.0

        // Max anti-velocity used when trying to stop the ship
        var linearStabilizeMaxAntiVelocity = 1.0

        // Anti-velocity mass relevance when stopping the ship
        // Max 10.0 (means no mass irrelevance)
        var antiVelocityMassRelevance = 0.8

        // Blacklist of blocks that don't get added for ship building
        var blockBlacklist = setOf(
            "vs_eureka:ship_helm",
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:stone",
            "minecraft:bedrock",
            "minecraft:sand",
            "minecraft:gravel",
            "minecraft:water",
            "minecraft:lava",
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
            "minecraft:air"
        )

        val diagonals = true
        val assembliesPerTick = 1000
    }
}
