package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.CreativeTabs
import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

@Suppress("unused")
object Items {
    private val ITEMS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.ITEM_REGISTRY)
    private val EUREKA_TAB: CreativeModeTab = CreativeTabs.create(ResourceLocation(EurekaMod.MOD_ID, "eureka_tab")) { ItemStack(SHIP_HELM.get()) }

    val SHIP_HELM = ITEMS.register("ship_helm") { Item(Item.Properties().tab(EUREKA_TAB)) }!!


    fun register() {
        ITEMS.register()
    }
}