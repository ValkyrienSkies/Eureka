package org.valkyrienskies.eureka.block

import net.minecraft.util.StringRepresentable
import net.minecraft.world.level.block.Block

public interface IWoodType : StringRepresentable {

    fun getWood(): Block

    fun getPlanks(): Block
}