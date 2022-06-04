package org.valkyrienskies.eureka.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import org.valkyrienskies.eureka.EurekaMod;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EurekaMod.MOD_ID)
public class EurekaModForge {
    public EurekaModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(EurekaMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        EurekaMod.init();
    }
}
