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
    }
}
