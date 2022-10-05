package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
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
import org.valkyrienskies.eureka.block.AnchorBlock
import org.valkyrienskies.eureka.block.BallastBlock
import org.valkyrienskies.eureka.block.BalloonBlock
import org.valkyrienskies.eureka.block.EngineBlock
import org.valkyrienskies.eureka.block.FloaterBlock
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.block.WoodType
import org.valkyrienskies.eureka.mixin.world.level.block.FireBlockInvoker

@Suppress("unused")
object EurekaBlocks {
    private val BLOCKS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val ANCHOR = AnchorBlock byName "anchor"
    val ENGINE = EngineBlock byName "engine"
    val FLOATER = FloaterBlock byName "floater"
    val BALLAST = BallastBlock byName "ballast"
    
    // region Ship Helms
    val OAK_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.OAK
    ) byName "oak_ship_helm"
    val SPRUCE_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.SPRUCE
    ) byName "spruce_ship_helm"
    val BIRCH_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.BIRCH
    ) byName "birch_ship_helm"
    val JUNGLE_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.JUNGLE
    ) byName "jungle_ship_helm"
    val ACACIA_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.ACACIA
    ) byName "acacia_ship_helm"
    val DARK_OAK_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD),
        WoodType.DARK_OAK
    ) byName "dark_oak_ship_helm"
    val CRIMSON_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM),
        WoodType.CRIMSON
    ) byName "crimson_ship_helm"
    val WARPED_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM),
        WoodType.WARPED
    ) byName "warped_ship_helm"
    // endregion

    // region Balloons
    val BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.WOOL).sound(SoundType.WOOL)
    ) byName "balloon"
    val WHITE_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.SNOW).sound(SoundType.WOOL)
    ) byName "white_balloon"
    val LIGHT_GRAY_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GRAY).sound(SoundType.WOOL)
    ) byName "light_gray_balloon"
    val GRAY_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GRAY).sound(SoundType.WOOL)
    ) byName "gray_balloon"
    val BLACK_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLACK).sound(SoundType.WOOL)
    ) byName "black_balloon"
    val RED_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_RED).sound(SoundType.WOOL)
    ) byName "red_balloon"
    val ORANGE_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_ORANGE).sound(SoundType.WOOL)
    ) byName "orange_balloon"
    val YELLOW_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_YELLOW).sound(SoundType.WOOL)
    ) byName "yellow_balloon"
    val LIME_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_GREEN).sound(SoundType.WOOL)
    ) byName "lime_balloon"
    val GREEN_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_GREEN).sound(SoundType.WOOL)
    ) byName "green_balloon"
    val LIGHT_BLUE_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_LIGHT_BLUE).sound(SoundType.WOOL)
    ) byName "light_blue_balloon"
    val CYAN_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_CYAN).sound(SoundType.WOOL)
    ) byName "cyan_balloon"
    val BLUE_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BLUE).sound(SoundType.WOOL)
    ) byName "blue_balloon"
    val PURPLE_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PURPLE).sound(SoundType.WOOL)
    ) byName "purple_balloon"
    val MAGENTA_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_MAGENTA).sound(SoundType.WOOL)
    ) byName "magenta_balloon"
    val PINK_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_PINK).sound(SoundType.WOOL)
    ) byName "pink_balloon"
    val BROWN_BALLOON = BalloonBlock(
        BlockBehaviour.Properties.of(Material.WOOL, MaterialColor.COLOR_BROWN).sound(SoundType.WOOL)
    ) byName "brown_balloon"
    // endregion

    fun register() {
        BLOCKS.register()
        makeFlammables()
    }

    fun flammableBlock(block: Block?, flameOdds: Int, burnOdds: Int) {
        val fire = Blocks.FIRE as FireBlock
        (fire as FireBlockInvoker).invokeSetFlammable(block, flameOdds, burnOdds)
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

    // Blocks should also be registered as items, if you want them to be able to be held
    // aka all blocks
    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.iterator().forEach {
            items.register(it.id) { BlockItem(it.get(), Item.Properties().tab(EurekaItems.TAB)) }
        }
    }

    private infix fun Block.byName(name: String) = BLOCKS.register(name) { this }
}
