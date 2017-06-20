package com.cout970.magneticraft.tileentity.modules.conveyor_belt

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.util.vector.vec2Of

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

    fun transform(x: Int, y: Int): IVector2 {
        return when (this) {
            OUT_FRONT -> vec2Of(
                    x, y + 16)
            OUT_BACK -> vec2Of(
                    16 - x, (16 - y) + 16)
            OUT_LEFT -> vec2Of(
                    -(y + 1), x)
            OUT_RIGHT -> vec2Of(
                    16 + y, 16 - (x + 1))
            IN_BACK -> vec2Of(
                    x, y - 16)
            IN_LEFT -> vec2Of(
                    y, -(x + 1))
            IN_RIGHT -> vec2Of(
                    16 - (y + 1), x - 16)
        }
    }
}