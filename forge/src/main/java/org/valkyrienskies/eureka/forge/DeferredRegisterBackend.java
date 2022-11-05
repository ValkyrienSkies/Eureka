package org.valkyrienskies.eureka.forge;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.registry.DeferredRegister;

public class DeferredRegisterBackend implements DeferredRegister.DeferredRegisterBackend {

    @NotNull
    @Override
    public <T> DeferredRegister<T> makeDeferredRegister(@NotNull String id, @NotNull ResourceKey<Registry<T>> registry) {
        return new DeferredRegisterImpl(id, registry);
    }
}
