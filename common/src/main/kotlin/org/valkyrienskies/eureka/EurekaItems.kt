package org.valkyrienskies.eureka

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.eureka.registry.CreativeTabs
import org.valkyrienskies.eureka.registry.DeferredRegister

@Suppress("unused")
object EurekaItems {
    private val ITEMS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.ITEM_REGISTRY)
    val TAB: CreativeModeTab = CreativeTabs.create(
        ResourceLocation(
            EurekaMod.MOD_ID,
            "eureka_tab"
        )
    ) { ItemStack(EurekaBlocks.OAK_SHIP_HELM.get()) }

    fun register() {
        EurekaBlocks.registerItems(ITEMS)
        ITEMS.applyAll()
    }

    private infix fun Item.byName(name: String) = ITEMS.register(name) { this }
}
