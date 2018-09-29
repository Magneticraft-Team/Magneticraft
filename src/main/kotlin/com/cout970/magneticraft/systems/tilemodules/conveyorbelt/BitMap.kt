package com.cout970.magneticraft.systems.tilemodules.conveyorbelt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of

open class BitMap(val map: BooleanArray = BooleanArray(16 * 16)) : IBitMap {

    override operator fun get(x: Int, y: Int): Boolean {
        val index = x + y * 16
        if (index < 0 || index >= 16 * 16) return false
        return map[index]
    }

    override operator fun set(x: Int, y: Int, value: Boolean) {
        val index = x + y * 16
        if (index < 0 || index >= 16 * 16) return
        map[index] = value
    }

    override fun mark(box: AABB) {
        mark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
    }

    override fun mark(start: IVector2, end: IVector2) {
        for (i in Math.floor(start.x).toInt() until Math.ceil(end.x).toInt()) {
            for (j in Math.floor(start.y).toInt() until Math.ceil(end.y).toInt()) {
                this[i, j] = true
            }
        }
    }

    override fun unmark(box: AABB) {
        unmark(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
    }

    override fun unmark(start: IVector2, end: IVector2) {
        for (i in Math.floor(start.x).toInt() until Math.ceil(end.x).toInt()) {
            for (j in Math.floor(start.y).toInt() until Math.ceil(end.y).toInt()) {
                this[i, j] = false
            }
        }
    }

    // Returns true if there and empty space in the hitbox area
    override fun test(box: AABB): Boolean {
        return test(vec2Of(box.minX, box.minZ) * 16, vec2Of(box.maxX, box.maxZ) * 16)
    }

    override fun test(start: IVector2, end: IVector2): Boolean {
        for (i in Math.floor(start.x).toInt() until Math.ceil(end.x).toInt()) {
            for (j in Math.floor(start.y).toInt() until Math.ceil(end.y).toInt()) {
                if (this[i, j]) return false
            }
        }
        return true
    }

    override fun clear() {
        for (i in 0 until 16 * 16) {
            map[i] = false
        }
    }

    override fun copy(): BitMap {
        return BitMap(map.clone())
    }

    override fun toString(): String {
        return buildString {
            append("BitMap(\n")
            for (i in 0 until 16) {
                for (j in 0 until 16) {
                    if (this@BitMap[i, j]) {
                        append("#")
                    } else {
                        append("_")
                    }
                }
                append('\n')
            }
            append(")")
        }
    }
}