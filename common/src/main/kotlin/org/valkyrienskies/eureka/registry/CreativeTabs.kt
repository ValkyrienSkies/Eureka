package org.valkyrienskies.eureka.registry

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.eureka.EurekaBlocks
import org.valkyrienskies.eureka.EurekaItems

object CreativeTabs {
    fun create(): CreativeModeTab {
        return CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
            .title(Component.translatable("itemGroup.eureka"))
            .icon { ItemStack(EurekaBlocks.OAK_SHIP_HELM.get().asItem()) }
            .displayItems { _, output ->
                EurekaItems.ITEMS.forEach {
                    output.accept(it.get())
                }
            }
            .build()
    }
}
