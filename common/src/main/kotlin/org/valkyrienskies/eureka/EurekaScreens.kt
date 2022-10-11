package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import org.valkyrienskies.eureka.gui.engine.EngineScreenMenu
import org.valkyrienskies.eureka.gui.shiphelm.ShipHelmScreenMenu

private typealias HFactory<T> = (syncId: Int, playerInv: Inventory) -> T

@Suppress("unused")
object EurekaScreens {
    private val SCREENS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.MENU_REGISTRY)

    val SHIP_HELM = ShipHelmScreenMenu.factory withName "ship_helm"
    val ENGINE = EngineScreenMenu.factory withName "engine"

    fun register() {
        SCREENS.register()
    }

    private infix fun <T : AbstractContainerMenu> HFactory<T>.withName(name: String) =
        SCREENS.register(name) { MenuType(this) }
}
