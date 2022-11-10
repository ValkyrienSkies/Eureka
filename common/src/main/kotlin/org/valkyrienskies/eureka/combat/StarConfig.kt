package org.valkyrienskies.eureka.combat

import net.minecraft.world.level.Explosion
import net.minecraft.world.phys.Vec3

class StarConfig(var size: Float,
                          var causesFire: Boolean,
                          var power: Double,
                          var direction: Boolean,
                          var angle: Double,
                          var knockback: Double,
                          var interaction: Explosion.BlockInteraction) {

}