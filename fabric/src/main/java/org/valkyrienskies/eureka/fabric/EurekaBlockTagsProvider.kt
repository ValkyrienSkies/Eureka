package org.valkyrienskies.eureka.fabric

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider
import net.minecraft.core.HolderLookup
import net.minecraft.tags.BlockTags
import org.valkyrienskies.eureka.EurekaBlocks
import java.util.concurrent.CompletableFuture

class EurekaBlockTagsProvider(output: FabricDataOutput, registriesFuture: CompletableFuture<HolderLookup.Provider>) :
    FabricTagProvider.BlockTagProvider(output, registriesFuture) {
    override fun addTags(arg: HolderLookup.Provider) {
        getOrCreateTagBuilder(BlockTags.WOOL)
            .add(EurekaBlocks.BALLOON.get())
            .add(EurekaBlocks.WHITE_BALLOON.get())
            .add(EurekaBlocks.LIGHT_GRAY_BALLOON.get())
            .add(EurekaBlocks.GRAY_BALLOON.get())
            .add(EurekaBlocks.BLACK_BALLOON.get())
            .add(EurekaBlocks.RED_BALLOON.get())
            .add(EurekaBlocks.ORANGE_BALLOON.get())
            .add(EurekaBlocks.YELLOW_BALLOON.get())
            .add(EurekaBlocks.LIME_BALLOON.get())
            .add(EurekaBlocks.GREEN_BALLOON.get())
            .add(EurekaBlocks.LIGHT_BLUE_BALLOON.get())
            .add(EurekaBlocks.CYAN_BALLOON.get())
            .add(EurekaBlocks.BLUE_BALLOON.get())
            .add(EurekaBlocks.PURPLE_BALLOON.get())
            .add(EurekaBlocks.MAGENTA_BALLOON.get())
            .add(EurekaBlocks.PINK_BALLOON.get())
            .add(EurekaBlocks.BROWN_BALLOON.get())
            .add(EurekaBlocks.FLOATER.get())

        getOrCreateTagBuilder(BlockTags.PLANKS)
            .add(EurekaBlocks.OAK_SHIP_HELM.get())
            .add(EurekaBlocks.SPRUCE_SHIP_HELM.get())
            .add(EurekaBlocks.BIRCH_SHIP_HELM.get())
            .add(EurekaBlocks.JUNGLE_SHIP_HELM.get())
            .add(EurekaBlocks.ACACIA_SHIP_HELM.get())
            .add(EurekaBlocks.DARK_OAK_SHIP_HELM.get())
            .add(EurekaBlocks.CRIMSON_SHIP_HELM.get())
            .add(EurekaBlocks.WARPED_SHIP_HELM.get())

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_AXE)
            .add(EurekaBlocks.OAK_SHIP_HELM.get())
            .add(EurekaBlocks.SPRUCE_SHIP_HELM.get())
            .add(EurekaBlocks.BIRCH_SHIP_HELM.get())
            .add(EurekaBlocks.JUNGLE_SHIP_HELM.get())
            .add(EurekaBlocks.ACACIA_SHIP_HELM.get())
            .add(EurekaBlocks.DARK_OAK_SHIP_HELM.get())
            .add(EurekaBlocks.CRIMSON_SHIP_HELM.get())
            .add(EurekaBlocks.WARPED_SHIP_HELM.get())

        getOrCreateTagBuilder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(EurekaBlocks.ANCHOR.get())
            .add(EurekaBlocks.ENGINE.get())
            .add(EurekaBlocks.BALLAST.get())
    }
}
