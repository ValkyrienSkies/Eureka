package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import org.valkyrienskies.eureka.block.AnchorBlock
import org.valkyrienskies.eureka.block.BallastBlock
import org.valkyrienskies.eureka.block.BalloonBlock
import org.valkyrienskies.eureka.block.EngineBlock
import org.valkyrienskies.eureka.block.FloaterBlock
import org.valkyrienskies.eureka.block.ShipHelmBlock

@Suppress("unused")
object EurekaBlocks {
    private val BLOCKS = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val OAK_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "oak_ship_helm"
    val SPRUCE_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "spruce_ship_helm"
    val BIRCH_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "birch_ship_helm"
    val JUNGLE_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "jungle_ship_helm"
    val ACACIA_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "acacia_ship_helm"
    val DARK_OAK_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.WOOD).strength(2.5F).sound(SoundType.WOOD)
    ) byName "dark_oak_ship_helm"
    val CRIMSON_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM)
    ) byName "crimson_ship_helm"
    val WARPED_SHIP_HELM = ShipHelmBlock(
        BlockBehaviour.Properties.of(Material.NETHER_WOOD).strength(2.5F).sound(SoundType.STEM)
    ) byName "warped_ship_helm"
    val ANCHOR = AnchorBlock byName "anchor"
    val ENGINE = EngineBlock byName "engine"
    val BALLOON = BalloonBlock byName "balloon"
    val FLOATER = FloaterBlock byName "floater"
    val BALLAST = BallastBlock byName "ballast"

    fun register() {
        BLOCKS.register()
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
