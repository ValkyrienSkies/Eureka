package org.valkyrienskies.eureka.fabric;

import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.eureka.EurekaMod;

public class EurekaModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        EurekaMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            EurekaMod.initClient();
        }
    }

    public static class ModMenu implements ModMenuApi {
        /* TODO
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSConfig
        }*/
    }
}
