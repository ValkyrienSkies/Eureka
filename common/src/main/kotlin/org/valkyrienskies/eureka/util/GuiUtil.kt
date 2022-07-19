package org.valkyrienskies.eureka.util

import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.ContainerData
import net.minecraft.world.inventory.Slot
import kotlin.reflect.KProperty

fun AbstractContainerMenu.inventorySlots(addSlot: (Slot) -> Slot, inventory: Inventory) {
    repeat(3) { j ->
        repeat(9) { k ->
            addSlot(Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18))
        }
    }

    repeat(9) { j ->
        addSlot(Slot(inventory, j, 8 + j * 18, 142))
    }
}

interface IKtContainerData : ContainerData {
    operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate
}

class KtContainerData : IKtContainerData {
    private var values = IntArrayList()

    override operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate {
        values.add(0)
        return ContainerDataDelegate(values.size - 1, this)
    }

    override fun get(i: Int): Int = values.getInt(i)

    override fun set(i: Int, value: Int) {
        values.set(i, value)
    }

    override fun getCount(): Int = values.size

    fun clone(): ClonedKtContainerData = ClonedKtContainerData(this)
}

class ClonedKtContainerData(val parent: KtContainerData) : IKtContainerData {
    private var count = 0

    override operator fun provideDelegate(thisRef: Any, property: KProperty<*>): ContainerDataDelegate {
        return ContainerDataDelegate(count++, this)
    }

    override fun get(i: Int): Int = parent.get(i)

    override fun set(i: Int, value: Int) = parent.set(i, value)

    override fun getCount(): Int = parent.count
}

class ContainerDataDelegate(private val id: Int, private val containerData: ContainerData) {

    operator fun getValue(thisRef: Any, property: KProperty<*>): Int = containerData.get(id)

    operator fun setValue(thisRef: Any, property: KProperty<*>, value: Int) = containerData.set(id, value)
}
