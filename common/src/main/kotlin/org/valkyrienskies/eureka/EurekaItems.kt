package org.valkyrienskies.eureka

import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.eureka.registry.RegistrySupplier

@Suppress("unused")
object EurekaItems {
    private val ITEMS = DeferredRegister.create(EurekaMod.MOD_ID, Registries.ITEM)
    val TAB: ResourceKey<CreativeModeTab> =
        ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation(EurekaMod.MOD_ID, "eureka_tab"))

    fun register() {
        EurekaBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    fun createCreativeTab(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.eureka"))
            .icon { ItemStack(EurekaBlocks.OAK_SHIP_HELM.get()) }
            .displayItems { _, output ->
                ITEMS.forEach { registrySupplier: RegistrySupplier<Item> ->
                    output.accept(registrySupplier.get())
                }
            }
            .build()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
