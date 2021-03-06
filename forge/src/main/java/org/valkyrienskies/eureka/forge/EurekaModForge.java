package org.valkyrienskies.eureka.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.eureka.EurekaMod;

@Mod(EurekaMod.MOD_ID)
public class EurekaModForge {
    public EurekaModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(EurekaMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);
        EurekaMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        EurekaMod.initClient();
    }
}
