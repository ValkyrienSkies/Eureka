package org.valkyrienskies.eureka.forge;

import java.util.Iterator;
import kotlin.jvm.functions.Function0;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.eureka.registry.DeferredRegister;
import org.valkyrienskies.eureka.registry.RegistrySupplier;

public class DeferredRegisterImpl<T> implements DeferredRegister<T> {
    private final net.minecraftforge.registries.DeferredRegister<T> forge;

    public DeferredRegisterImpl(final String modId, final ResourceKey<Registry<T>> registry) {
        forge = net.minecraftforge.registries.DeferredRegister.create(registry.location(), modId);
    }

    @NotNull
    @Override
    public <I extends T> RegistrySupplier<I> register(
            @NotNull final String name,
            @NotNull final Function0<? extends I> builder
    ) {
        final RegistryObject<I> result = forge.register(name, builder::invoke);

        return new RegistrySupplier<I>() {
            @NotNull
            @Override
            public String getName() {
                return name;
            }

            @Override
            public I get() {
                return result.get();
            }
        };
    }

    @Override
    public void applyAll() {
        forge.register(EurekaModForge.MOD_BUS);
    }

    @NotNull
    @Override
    public Iterator<RegistrySupplier<T>> iterator() {
        final Iterator<RegistryObject<T>> iterator = forge.getEntries().iterator();

        return new Iterator<RegistrySupplier<T>>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public RegistrySupplier<T> next() {
                final RegistryObject<T> result = iterator.next();

                return new RegistrySupplier<T>() {
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
