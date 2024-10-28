package org.valkyrienskies.eureka.registry;

import net.minecraft.world.item.ItemStack;

public abstract class FuelRegistry {
    public static FuelRegistry INSTANCE = null;

    public abstract int get(ItemStack stack);
}
