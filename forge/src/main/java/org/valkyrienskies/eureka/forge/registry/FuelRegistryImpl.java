package org.valkyrienskies.eureka.forge.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.event.EventHooks;

public class FuelRegistryImpl extends org.valkyrienskies.eureka.registry.FuelRegistry {

    public FuelRegistryImpl() {
        INSTANCE = this;
    }

    @Override
    public int get(ItemStack stack) {
        return EventHooks.getItemBurnTime(stack, stack.getBurnTime(RecipeType.SMELTING), RecipeType.SMELTING);
    }
}
