package org.valkyrienskies.eureka.forge.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeHooks;

public class FuelRegistryImpl extends org.valkyrienskies.eureka.registry.FuelRegistry {

    public FuelRegistryImpl() {
        INSTANCE = this;
    }

    @Override
    public int get(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null);
    }
}
