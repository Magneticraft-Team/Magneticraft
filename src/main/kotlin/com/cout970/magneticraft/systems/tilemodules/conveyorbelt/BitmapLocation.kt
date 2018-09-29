package com.cout970.magneticraft.systems.tilemodules.conveyorbelt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of

enum class BitmapLocation {
    OUT_FRONT,
    OUT_BACK,
    OUT_LEFT,
    OUT_RIGHT,
    IN_BACK,
    IN_LEFT,
    IN_RIGHT;

    fun isInside(x: Int, y: Int): Boolean {
        return when (this) {
            OUT_FRONT -> x in 0..15 && y in -1 downTo -16
            OUT_BACK -> x in 0..15 && y in -1 downTo -16
            OUT_LEFT -> x in 0..15 && y in -1 downTo -16
            OUT_RIGHT -> x in 0..15 && y in -1 downTo -16
            IN_BACK -> x in 0..15 && y in 16..31
            IN_LEFT -> x in -1 downTo -16 && y in 0..15
            IN_RIGHT -> x in 16..31 && y in 0..15
        }
    }

    fun fromLocalToExternal(x: Int, y: Int): IVector2 {
        return when (this) {
            OUT_FRONT -> vec2Of(x, y + 16)
            OUT_BACK -> vec2Of(16 - x, (16 - y) + 16)
            OUT_LEFT -> vec2Of(-(y + 1), x)
            OUT_RIGHT -> vec2Of(16 + y, 16 - (x + 1))
            IN_BACK -> vec2Of(x, y - 16)
            IN_LEFT -> vec2Of(y, -(x + 1))
            IN_RIGHT -> vec2Of(16 - (y + 1), x - 16)
        }
    }

    fun fromExternalToLocal(box: AABB): AABB {
        val min = fromExternalToLocal(box.minX * 16, box.minZ * 16) / 16
        val max = fromExternalToLocal(box.maxX * 16, box.maxZ * 16) / 16
        return AABB(
            min.x, box.minY, min.y,
            max.x, box.maxY, max.y
        )
    }

    fun fromExternalToLocal(x: Double, y: Double): IVector2 {
        return when (this) {
            OUT_FRONT -> vec2Of(x, y - 16)
            OUT_BACK -> vec2Of(16 - x, 16 + 16 - y)
            OUT_LEFT -> vec2Of(y, -x)
            OUT_RIGHT -> vec2Of(15 - y + 1, x - 16)
            IN_BACK -> vec2Of(x, y + 16)
            IN_LEFT -> vec2Of(-y, x)
            IN_RIGHT -> vec2Of(y + 16, 15 - x + 1)
        }
    }
}