package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.client.gui.screen.ingame.HandledScreen
import net.minecraft.client.gui.screen.ingame.HandledScreens
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.screen.ScreenHandler
import net.minecraft.screen.ScreenHandlerType
import net.minecraft.text.Text
import net.minecraft.util.registry.Registry
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreen
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenHandler
import kotlin.reflect.KClass

private typealias HFactory<T> = (syncId: Int, playerInv: PlayerInventory) -> T
private typealias SFactory<T> = (handler: T, playerInv: PlayerInventory, text: Text) -> HandledScreen<T>
private data class ClientScreenRegistar<T: ScreenHandler>(
    val type: RegistrySupplier<ScreenHandlerType<T>>,
    val factory: SFactory<T>) {
    fun register() = HandledScreens.register(type.get(), factory)
}

@Suppress("unused")
object EurekaScreens {
    private val SCREENS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.MENU_KEY)
    private val SCREENS_CLIENT = mutableListOf<ClientScreenRegistar<*>>()

    val SHIP_HELM = ::ShipHelmScreenHandler withScreen ::ShipHelmScreen withName "ship_helm"

    fun register() {
        SCREENS.register()
    }

    fun registerClient() {
        SCREENS_CLIENT.forEach { it.register() }
    }

    private infix fun <T: ScreenHandler> HFactory<T>.withScreen(screen: SFactory<T>) = Pair(this, screen)
    private infix fun <T: ScreenHandler> Pair<HFactory<T>, SFactory<T>>.withName(name: String) =
        SCREENS.register(name) { ScreenHandlerType(this.first) }.also {
            SCREENS_CLIENT.add(ClientScreenRegistar(it, this.second))
        }
}