package org.valkyrienskies.eureka.gui.engine

import net.minecraft.world.Container
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity

class FuelSlot(container: Container, slot: Int, x: Int, y: Int) : Slot(container, slot, x, y) {
    override fun mayPlace(stack: ItemStack): Boolean {
        return AbstractFurnaceBlockEntity.isFuel(stack) || isBucket(stack)
    }

    override fun getMaxStackSize(stack: ItemStack): Int {
        return if (isBucket(stack)) 1 else super.getMaxStackSize(stack)
    }

    private fun isBucket(stack: ItemStack): Boolean {
        return stack.item === Items.BUCKET
    }
}
