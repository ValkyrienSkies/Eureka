package org.valkyrienskies.eureka.block

import net.minecraft.world.level.block.Block

public interface IWoodType {

    fun getWood(): Block

    fun getPlanks(): Block

    fun getSerializedName(): String

}