package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import me.shedaniel.architectury.registry.RegistrySupplier
import net.minecraft.Util
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import org.valkyrienskies.eureka.block.EngineBlock
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.blockentity.EngineBlockEntity
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

@Suppress("unused")
object EurekaBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    private infix fun RegistrySupplier<out Block>.and(supplier: RegistrySupplier<out Block>): Set<() -> Block> =
            setOf(this::get, supplier::get)

    private infix fun Set<() -> Block>.and(supplier: RegistrySupplier<out Block>): Set<() -> Block> =
            this.plus(supplier::get)

    private infix fun Set<() -> Block>.withBE(blockEntity: () -> BlockEntity) = Pair(this, blockEntity)
    private infix fun RegistrySupplier<out Block>.withBE(blockEntity: () -> BlockEntity) =
            Pair(setOf(this::get), blockEntity)

    val SHIP_HELM = EurekaBlocks.OAK_SHIP_HELM withBE ShipHelmBlockEntity.supplier byName "ship_helm"
    val ENGINE = EurekaBlocks.ENGINE withBE EngineBlockEntity.supplier byName "engine"

    fun register() {
        BLOCKENTITIES.register()
    }

    private infix fun Block.withBE(blockEntity: () -> BlockEntity) = Pair(this, blockEntity)
    private infix fun Pair<Set<() -> Block>, () -> BlockEntity>.byName(name: String) =
            BLOCKENTITIES.register(name) {
                val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)
                BlockEntityType.Builder.of(this.second, *this.first.map { it() }.toTypedArray()).build(type)
            }
}
