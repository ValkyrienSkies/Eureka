package org.valkyrienskies.eureka.util

import net.minecraft.core.Direction
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import kotlin.math.max
import kotlin.math.min

interface RotShape {
    fun rotate90(): RotShape
    fun rotate180(): RotShape = rotate90().rotate90()
    fun rotate270(): RotShape = rotate180().rotate90()
    fun makeMcShape(): VoxelShape
    fun build(): VoxelShape = makeMcShape().optimize()
}

class DirectionalShape(shape: RotShape) {
    val north = shape.build()
    val east = shape.rotate90().build()
    val south = shape.rotate180().build()
    val west = shape.rotate270().build()

    operator fun get(direction: Direction): VoxelShape = when (direction) {
        Direction.NORTH -> north
        Direction.EAST -> east
        Direction.SOUTH -> south
        Direction.WEST -> west
        else -> throw IllegalArgumentException()
    }
}

object RotShapes {
    fun box(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double): RotShape =
        Box(x1, y1, z1, x2, y2, z2)

    fun or(vararg shapes: RotShape): RotShape = Union(shapes.asList())

    private class Box(val x1: Double, val y1: Double, val z1: Double, val x2: Double, val y2: Double, val z2: Double) :
        RotShape {
        override fun rotate90(): RotShape = Box(16 - z1, y1, x1, 16 - z2, y2, x2)

        override fun makeMcShape(): VoxelShape = Shapes.box(
            min(x1, x2) / 16,
            min(y1, y2) / 16,
            min(z1, z2) / 16,
            max(x1, x2) / 16,
            max(y1, y2) / 16,
            max(z1, z2) / 16)
    }

    private class Union(val shapes: List<RotShape>) : RotShape {
        override fun rotate90(): RotShape = Union(shapes.map { it.rotate90() })

        override fun makeMcShape(): VoxelShape = shapes.fold(Shapes.empty()) { mc, n -> Shapes.or(mc, n.makeMcShape()) }
    }
}
