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
        var impulseAlleviationRate = 2.3

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
            "minecraft:air"
        )
    }
}
