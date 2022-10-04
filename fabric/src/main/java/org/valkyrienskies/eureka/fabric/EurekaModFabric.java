package org.valkyrienskies.eureka.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import org.valkyrienskies.core.config.VSConfigClass;
import org.valkyrienskies.eureka.EurekaBlockEntities;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

public class EurekaModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        try {
            Class.forName("org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        EurekaMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            EurekaMod.initClient();
            BlockEntityRendererRegistry.INSTANCE.register(
                    EurekaBlockEntities.INSTANCE.getSHIP_HELM().get(),
                    ShipHelmBlockEntityRenderer::new
            );
        }
    }

    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSClothConfig.createConfigScreenFor(
                    parent,
                    VSConfigClass.Companion.getRegisteredConfig(EurekaConfig.class)
            );
        }
    }
}
