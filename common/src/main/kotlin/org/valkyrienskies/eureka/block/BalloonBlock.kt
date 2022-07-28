package org.valkyrienskies.eureka.block

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.material.Material
import net.minecraft.world.level.material.MaterialColor

object BalloonBlock : Block(
    Properties.of(Material.WOOL, MaterialColor.WOOL).sound(SoundType.WOOL)
) {

    override fun fallOn(level: Level, blockPos: BlockPos, entity: Entity, f: Float) {
        entity.causeFallDamage(f, 0.2f)
    }
}
