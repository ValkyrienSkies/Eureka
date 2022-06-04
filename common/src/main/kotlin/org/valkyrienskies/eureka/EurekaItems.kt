package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.CreativeTabs
import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack

@Suppress("unused")
object EurekaItems {
    private val ITEMS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabs.create(ResourceLocation(EurekaMod.MOD_ID, "eureka_tab")) { ItemStack(EurekaBlocks.SHIP_HELM.get()) }

    fun register() {
        EurekaBlocks.registerItems(ITEMS)
        ITEMS.register()
    }
}