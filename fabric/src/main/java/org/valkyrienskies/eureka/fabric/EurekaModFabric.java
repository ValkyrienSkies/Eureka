package org.valkyrienskies.eureka.fabric;

import org.valkyrienskies.eureka.EurekaMod;
import net.fabricmc.api.ModInitializer;

public class EurekaModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EurekaMod.init();
    }
}
