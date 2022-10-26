package org.valkyrienskies.eureka.fabric.mixin.client;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.block.WoodType;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// TODO this is a temporary solution to the problem of json loading ship helm wheels
// look at forge implementation for a better solution
@Mixin(ModelBakery.class)
public abstract class MixinModelBakery {

    @Shadow
    @Final
    private static Logger LOGGER;
    @Shadow
    @Final
    private ResourceManager resourceManager;
    @Shadow
    @Final
    private BlockModelDefinition.Context context;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> topLevelModels;
    @Shadow
    @Final
    private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Inject(method = "<init>", at = @At(value = "TAIL"))
    private void addCustomStaticDefinitions(final ResourceManager resourceManager,
                                            final BlockColors blockColors,
                                            final ProfilerFiller profilerFiller,
                                            final int i,
                                            final CallbackInfo ci) {
        try {
            final List<Pair<String, BlockModelDefinition>> definitions = this.resourceManager.getResources(
                    new ResourceLocation(EurekaMod.MOD_ID, "blockstates/ship_helm_wheel.json")).stream().map(resource -> {
                try (InputStream inputStream = resource.getInputStream()) {
                    final Pair<String, BlockModelDefinition> pair = Pair.of(resource.getSourceName(), BlockModelDefinition.fromStream(this.context, new InputStreamReader(inputStream, StandardCharsets.UTF_8)));
                    return pair;
                } catch (final Exception exception) {
                    throw new RuntimeException(
                            String.format("Exception loading blockstate definition: '%s' in resourcepack: '%s': %s",
                                    resource.getLocation(),
                                    resource.getSourceName(),
                                    exception.getMessage()));
                }
            }).collect(Collectors.toList());

            final List<ModelResourceLocation> takenStates = new ArrayList<>();

            definitions.forEach(p ->
                    p.getSecond().getVariants().forEach((String var, MultiVariant variant) ->
                            Arrays.stream(WoodType.values()).forEach(woodType -> {
                                ModelResourceLocation location = new ModelResourceLocation(
                                        new ResourceLocation(EurekaMod.MOD_ID, "ship_helm_wheel"),
                                        "wood=" + woodType.getResourceName()
                                );
                                if (location.getVariant().equals(var)) {
                                    this.unbakedCache.put(location, variant);
                                    this.topLevelModels.put(location, variant);
                                    takenStates.add(location);
                                }
                            })));

            Arrays.stream(WoodType.values()).forEach(woodType -> {
                ModelResourceLocation location = new ModelResourceLocation(
                        new ResourceLocation(EurekaMod.MOD_ID, "ship_helm_wheel"),
                        "wood=" + woodType.getResourceName()
                );

                if (!takenStates.contains(location)) {
                    LOGGER.warn("No model found for state: " + location);
                }
            });

        } catch (final Exception e) {
            throw new RuntimeException("Failed to load ship_helm_wheel", e);
        }
    }
}
