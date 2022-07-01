package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.core.Registry
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.valkyrienskies.eureka.gui.engine.EngineScreen
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreen
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu

private typealias HFactory<T> = (syncId: Int, playerInv: Inventory) -> T
private typealias SFactory<T> = (handler: T, playerInv: Inventory, text: Component) -> AbstractContainerScreen<T>

private data class ClientScreenRegistar<T : AbstractContainerMenu>(
    val type: RegistrySupplier<MenuType<T>>,
    val factory: SFactory<T>
) {
    fun register() = MenuScreens.register(type.get(), factory)
}

@Suppress("unused")
object EurekaScreens {
    private val SCREENS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.MENU_REGISTRY)
    private val SCREENS_CLIENT = mutableListOf<ClientScreenRegistar<*>>()

    val SHIP_HELM = ShipHelmScreenMenu.factory withScreen ::ShipHelmScreen withName "ship_helm"
    val ENGINE = EngineScreenMenu.factory withScreen ::EngineScreen withName "engine"
    fun register() {
        SCREENS.register()
    }

    fun registerClient() {
        SCREENS_CLIENT.forEach { it.register() }
    }

    private infix fun <T : AbstractContainerMenu> HFactory<T>.withScreen(screen: SFactory<T>) = Pair(this, screen)
    private infix fun <T : AbstractContainerMenu> Pair<HFactory<T>, SFactory<T>>.withName(name: String) =
        SCREENS.register(name) { MenuType(this.first) }.also {
            SCREENS_CLIENT.add(ClientScreenRegistar(it, this.second))
        }
}
