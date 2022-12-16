package org.valkyrienskies.eureka.forge;

import dan200.computercraft.api.ComputerCraftAPI;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.moddiscovery.ModInfo;
import org.valkyrienskies.core.config.VSConfigClass;
import org.valkyrienskies.eureka.EurekaBlockEntities;
import org.valkyrienskies.eureka.EurekaConfig;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.block.WoodType;
import org.valkyrienskies.eureka.blockentity.renderer.ShipHelmBlockEntityRenderer;
import org.valkyrienskies.eureka.blockentity.renderer.WheelModels;
import org.valkyrienskies.eureka.forge.integrations.cc_tweaked.EurekaPeripheralProviders;
import org.valkyrienskies.eureka.forge.integrations.cc_tweaked.ShipHelmPeripheralProvider;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(EurekaMod.MOD_ID)
public class EurekaModForge {
    boolean happendClientSetup = false;

    public EurekaModForge() {
        // Submit our event bus to let architectury register our content on the right time

        EventBuses.registerModEventBus(EurekaMod.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                () -> (Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(EurekaConfig.class))
        );

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModelRegistry);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        EurekaMod.init();

        if (FMLLoader.getLoadingModList().getModFileById("computercraft") != null && !EurekaConfig.SERVER.getComputerCraft().getDisableComputerCraft()) {
            EurekaPeripheralProviders.registerPeripheralProviders();
        }
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        EurekaMod.initClient();
        ClientRegistry.bindTileEntityRenderer(
                EurekaBlockEntities.INSTANCE.getSHIP_HELM().get(),
                ShipHelmBlockEntityRenderer::new
        );

        WheelModels.INSTANCE.setModelGetter(woodType -> ModelLoader.instance().getBakedTopLevelModels()
                .getOrDefault(
                        new ResourceLocation(EurekaMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"),
                        Minecraft.getInstance().getModelManager().getMissingModel()
                ));
    }

    void onModelRegistry(final ModelRegistryEvent event) {
        for (WoodType woodType : WoodType.values()) {
            ModelLoader.addSpecialModel(new ResourceLocation(EurekaMod.MOD_ID, "block/" + woodType.getResourceName() + "_ship_helm_wheel"));
        }
    }
}
