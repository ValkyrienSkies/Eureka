package org.valkyrienskies.eureka.forge;

import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.valkyrienskies.core.config.VSConfigClass;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(EurekaMod.MOD_ID)
public class EurekaModForge {
    public EurekaModForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(EurekaMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> (Minecraft client, Screen parent) ->
                VSClothConfig.createConfigScreenFor(parent,
                        VSConfigClass.Companion.getRegisteredConfig(EurekaConfig.class))
        );


        EurekaMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        EurekaMod.initClient();
    }
}
