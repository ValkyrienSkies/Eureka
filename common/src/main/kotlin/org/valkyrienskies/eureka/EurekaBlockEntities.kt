package org.valkyrienskies.eureka

import me.shedaniel.architectury.registry.DeferredRegister
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.datafixer.TypeReferences
import net.minecraft.util.Util
import net.minecraft.util.registry.Registry
import org.valkyrienskies.eureka.block.ShipHelmBlock
import org.valkyrienskies.eureka.blockentity.ShipHelmBlockEntity

@Suppress("unused")
object EurekaBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(EurekaMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_KEY)

    val SHIP_HELM = ShipHelmBlock withBE ShipHelmBlockEntity.supplier byName "ship_helm"

    fun register() {
        BLOCKENTITIES.register()
    }

    private infix fun Block.withBE(blockEntity: () -> BlockEntity) = Pair(this, blockEntity)
    private infix fun Pair<Block, () -> BlockEntity>.byName(name: String) =
        BLOCKENTITIES.register(name) {
            val type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, name)
            BlockEntityType.Builder.create(this.second, this.first).build(type)
        }
}