package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.Util
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

@Suppress("unused")
object EurekaBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    val SHIP_HELM = setOf(
        EurekaBlocks.OAK_SHIP_HELM,
        EurekaBlocks.SPRUCE_SHIP_HELM,
        EurekaBlocks.BIRCH_SHIP_HELM,
        EurekaBlocks.JUNGLE_SHIP_HELM,
        EurekaBlocks.ACACIA_SHIP_HELM,
        EurekaBlocks.DARK_OAK_SHIP_HELM,
        EurekaBlocks.CRIMSON_SHIP_HELM,
        EurekaBlocks.WARPED_SHIP_HELM
    ) withBE ShipHelmBlockEntity.supplier byName "ship_helm"

    val ENGINE = EurekaBlocks.ENGINE withBE EngineBlockEntity.supplier byName "engine"

    fun register() {
        BLOCKENTITIES.register()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: () -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: () -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: () -> T) = Pair(this, blockEntity)
    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, () -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)
            BlockEntityType.Builder.of(this.second, *this.first.map { it.get() }.toTypedArray()).build(type)
        }
}
