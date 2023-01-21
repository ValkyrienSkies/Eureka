package org.valkyrienskies.eureka

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor
import org.valkyrienskies.eureka.block.*
import org.valkyrienskies.eureka.registry.DeferredRegister
import org.valkyrienskies.mod.common.hooks.VSGameEvents

@Suppress("unused")
object EurekaBlocks {
    private val BLOCKS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val ANCHOR = BLOCKS.register("anchor", ::AnchorBlock)
    val ENGINE = BLOCKS.register("engine", ::EngineBlock)
    val FLOATER = BLOCKS.register("floater", ::FloaterBlock)
    val BALLAST = BLOCKS.register("ballast", ::BallastBlock)

    // region Ship Helms
    val OAK_SHIP_HELM = BLOCKS.register("oak_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.OAK
        )
    }
    val SPRUCE_SHIP_HELM = BLOCKS.register("spruce_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.SPRUCE
        )
    }
    val BIRCH_SHIP_HELM = BLOCKS.register("birch_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.BIRCH
        )
    }
    val JUNGLE_SHIP_HELM = BLOCKS.register("jungle_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.JUNGLE
        )
    }
    val ACACIA_SHIP_HELM = BLOCKS.register("acacia_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.ACACIA
        )
    }
    val DARK_OAK_SHIP_HELM = BLOCKS.register("dark_oak_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
            WoodType.DARK_OAK
        )
    }
    val CRIMSON_SHIP_HELM = BLOCKS.register("crimson_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM),
            WoodType.CRIMSON
        )
    }
    val WARPED_SHIP_HELM = BLOCKS.register("warped_ship_helm") {
        ShipHelmBlock(
            BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM),
            WoodType.WARPED
        )
    }
    // endregion

    // region Balloons
    val BALLOON = BLOCKS.register("balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.WOOL).sound(SoundType.WOOL)
        )
    }
    val WHITE_BALLOON = BLOCKS.register("white_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW).sound(SoundType.WOOL)
        )
    }
    val LIGHT_GRAY_BALLOON = BLOCKS.register("light_gray_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL)
        )
    }
    val GRAY_BALLOON = BLOCKS.register("gray_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY).sound(SoundType.WOOL)
        )
    }
    val BLACK_BALLOON = BLOCKS.register("black_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK).sound(SoundType.WOOL)
        )
    }
    val RED_BALLOON = BLOCKS.register("red_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED).sound(SoundType.WOOL)
        )
    }
    val ORANGE_BALLOON = BLOCKS.register("orange_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOL)
        )
    }
    val YELLOW_BALLOON = BLOCKS.register("yellow_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW).sound(SoundType.WOOL)
        )
    }
    val LIME_BALLOON = BLOCKS.register("lime_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL)
        )
    }
    val GREEN_BALLOON = BLOCKS.register("green_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN).sound(SoundType.WOOL)
        )
    }
    val LIGHT_BLUE_BALLOON = BLOCKS.register("light_blue_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL)
        )
    }
    val CYAN_BALLOON = BLOCKS.register("cyan_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN).sound(SoundType.WOOL)
        )
    }
    val BLUE_BALLOON = BLOCKS.register("blue_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE).sound(SoundType.WOOL)
        )
    }
    val PURPLE_BALLOON = BLOCKS.register("purple_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE).sound(SoundType.WOOL)
        )
    }
    val MAGENTA_BALLOON = BLOCKS.register("magenta_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA).sound(SoundType.WOOL)
        )
    }
    val PINK_BALLOON = BLOCKS.register("pink_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK).sound(SoundType.WOOL)
        )
    }
    val BROWN_BALLOON = BLOCKS.register("brown_balloon") {
        BalloonBlock(
            BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN).sound(SoundType.WOOL)
        )
    }
    // endregion

    fun register() {
        BLOCKS.applyAll()

        VSGameEvents.registriesCompleted.on { _, _ ->
            makeFlammables()
        }
    }

    // region Flammables
    // TODO make this part of the registration sequence
    fun flammableBlock(block: Block?, flameOdds: Int, burnOdds: Int) {
        val fire = Blocks.FIRE as FireBlock
        fire.setFlammable(block, flameOdds, burnOdds)
    }

    fun makeFlammables() {
        flammableBlock(OAK_SHIP_HELM.get(), 5, 20)
        flammableBlock(SPRUCE_SHIP_HELM.get(), 5, 20)
        flammableBlock(BIRCH_SHIP_HELM.get(), 5, 20)
        flammableBlock(JUNGLE_SHIP_HELM.get(), 5, 20)
        flammableBlock(ACACIA_SHIP_HELM.get(), 5, 20)
        flammableBlock(DARK_OAK_SHIP_HELM.get(), 5, 20)
        flammableBlock(BALLOON.get(), 30, 60)
        flammableBlock(WHITE_BALLOON.get(), 30, 60)
        flammableBlock(LIGHT_GRAY_BALLOON.get(), 30, 60)
        flammableBlock(GRAY_BALLOON.get(), 30, 60)
        flammableBlock(BLACK_BALLOON.get(), 30, 60)
        flammableBlock(RED_BALLOON.get(), 30, 60)
        flammableBlock(ORANGE_BALLOON.get(), 30, 60)
        flammableBlock(YELLOW_BALLOON.get(), 30, 60)
        flammableBlock(LIME_BALLOON.get(), 30, 60)
        flammableBlock(GREEN_BALLOON.get(), 30, 60)
        flammableBlock(LIGHT_BLUE_BALLOON.get(), 30, 60)
        flammableBlock(CYAN_BALLOON.get(), 30, 60)
        flammableBlock(BLUE_BALLOON.get(), 30, 60)
        flammableBlock(PURPLE_BALLOON.get(), 30, 60)
        flammableBlock(MAGENTA_BALLOON.get(), 30, 60)
        flammableBlock(PINK_BALLOON.get(), 30, 60)
        flammableBlock(BROWN_BALLOON.get(), 30, 60)
        flammableBlock(FLOATER.get(), 5, 20)
    }
    // endregion

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(EurekaItems.TAB)) }
        }
    }

}
