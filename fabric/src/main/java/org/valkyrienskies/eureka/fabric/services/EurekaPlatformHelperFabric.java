package org.valkyrienskies.eureka.fabric.services;

import kotlin.jvm.functions.Function0;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.services.EurekaPlatformHelper;

public class EurekaPlatformHelperFabric implements EurekaPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(
            @NotNull final ResourceLocation id,
            @NotNull final Function0<ItemStack> stack) {
        return FabricItemGroupBuilder.build(id, stack::invoke);
    }
}
