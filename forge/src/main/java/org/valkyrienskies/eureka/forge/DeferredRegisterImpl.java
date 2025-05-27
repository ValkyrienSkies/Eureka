package org.valkyrienskies.eureka.forge;

import java.util.Iterator;
import kotlin.jvm.functions.Function0;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.registry.DeferredRegister;
import org.valkyrienskies.eureka.registry.RegistrySupplier;

public class DeferredRegisterImpl<T> implements DeferredRegister<T> {
    private final net.neoforged.neoforge.registries.DeferredRegister<T> forge;

    public DeferredRegisterImpl(final String modId, final ResourceKey<Registry<T>> registry) {
        forge = net.neoforged.neoforge.registries.DeferredRegister.create(registry.location(), modId);
    }

    @NotNull
    @Override
    public <I extends T> RegistrySupplier<I> register(
        @NotNull final String name,
        @NotNull final Function0<? extends I> builder
    ) {
        final DeferredHolder<?, ?> result = forge.register(name, builder::invoke);

        return new RegistrySupplier<I>() {
            @NotNull
            @Override
            public String getName() {
                return name;
            }

            @Override
            public I get() {
                return (I) result.get();
            }
        };
    }

    @Override
    public void applyAll() {
        forge.register(EurekaModForge.INSTANCE.getModBus());
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        final Iterator<DeferredHolder<T, ?>> iterator = forge.getEntries().iterator();

        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public RegistrySupplier<T> next() {
                final DeferredHolder<T, ?> result = iterator.next();

                return new RegistrySupplier<>() {
                    @NotNull
                    @Override
                    public String getName() {
                        return result.getId().getPath();
                    }

                    @Override
                    public T get() {
                        return result.get();
                    }
                };
            }
        };
    }
}
