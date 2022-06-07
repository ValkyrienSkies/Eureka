package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.Util
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

@Suppress("unused")
object EurekaBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    val SHIP_HELM = ShipHelmBlock withBE ShipHelmBlockEntity.supplier byName "ship_helm"

    fun register() {
        BLOCKENTITIES.register()
    }

    private infix fun Block.withBE(blockEntity: () -> BlockEntity) = Pair(this, blockEntity)
    private infix fun Pair<Block, () -> BlockEntity>.byName(name: String) =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)
            BlockEntityType.Builder.of(this.second, this.first).build(type)
        }
}
