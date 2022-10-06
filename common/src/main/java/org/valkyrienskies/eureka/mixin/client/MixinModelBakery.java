package org.valkyrienskies.eureka.mixin.client;

import com.mojang.datafixers.util.Pair;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BlockModelDefinition;
import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.state.StateHolder;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.eureka.EurekaMod;
import org.valkyrienskies.eureka.blockentity.renderer.WheelModels;

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

            final List<StateHolder> takenStates = new ArrayList<>();

            definitions.forEach(p ->
                    p.getSecond().getVariants().forEach((String var, MultiVariant variant) ->
                            WheelModels.INSTANCE.getDefinition().getPossibleStates().forEach(state -> {
                                if (state.getLocation().getVariant().equals(var)) {
                                    this.unbakedCache.put(state.getLocation(), variant);
                                    this.topLevelModels.put(state.getLocation(), variant);
                                    takenStates.add(state);
                                }
                            })));

            WheelModels.INSTANCE.getDefinition().getPossibleStates().forEach(state -> {
                if (!takenStates.contains(state)) {
                    LOGGER.warn("No model found for state: " + state.getLocation());
                }
            });

        } catch (final Exception e) {
            throw new RuntimeException("Failed to load ship_helm_wheel", e);
        }
    }
}
