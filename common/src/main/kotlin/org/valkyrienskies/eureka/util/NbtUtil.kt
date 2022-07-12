package org.valkyrienskies.eureka.util

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag

fun CompoundTag.putBlockPos(prefix: String, blockPos: BlockPos?) =
    blockPos?.let {
        this.putInt(prefix + "x", it.x)
        this.putInt(prefix + "y", it.y)
        this.putInt(prefix + "z", it.z)
    }

fun CompoundTag.getBlockPos(prefix: String) =
    BlockPos(
        this.getInt(prefix + "x"),
        this.getInt(prefix + "y"),
        this.getInt(prefix + "z")
    )
