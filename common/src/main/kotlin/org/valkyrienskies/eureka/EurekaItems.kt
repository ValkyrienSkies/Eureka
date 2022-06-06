package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.CreativeTabs
import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry

@Suppress("unused")
object EurekaItems {
    private val ITEMS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.ITEM_KEY)
    val TAB: ItemGroup = CreativeTabs.create(Identifier(EurekaMod.MOD_ID, "eureka_tab")) { ItemStack(EurekaBlocks.SHIP_HELM.get()) }

    fun register() {
        EurekaBlocks.registerItems(ITEMS)
        ITEMS.register()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}