package org.valkyrienskies.eureka.forge.integration.cc_tweaked;

import dan200.computercraft.api.ComputerCraftAPI;

public class EurekaPeripheralProviders {
    public static void registerPeripheralProviders() {
        ComputerCraftAPI.registerPeripheralProvider(new ShipHelmPeripheralProvider());
    }
}
