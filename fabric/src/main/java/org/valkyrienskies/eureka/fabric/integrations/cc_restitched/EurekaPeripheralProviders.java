package org.valkyrienskies.eureka.fabric.integrations.cc_restitched;

import dan200.computercraft.api.ComputerCraftAPI;

public class EurekaPeripheralProviders {
    public static void registerPeripheralProviders() {
        ComputerCraftAPI.registerPeripheralProvider(new ShipHelmPeripheralProvider());
    }
}
