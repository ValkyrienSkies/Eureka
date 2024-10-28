package org.valkyrienskies.eureka.fabric.registry;

import net.fabricmc.fabric.api.registry.FuelRegistry;
import net.minecraft.world.item.ItemStack;

public class FuelRegistryImpl extends org.valkyrienskies.eureka.registry.FuelRegistry {
    public FuelRegistryImpl() {
        INSTANCE = this;
    }

    @Override
    public int get(ItemStack stack) {
        Integer time = FuelRegistry.INSTANCE.get(stack.getItem());
        return time == null ? 0 : time;
    }
}
