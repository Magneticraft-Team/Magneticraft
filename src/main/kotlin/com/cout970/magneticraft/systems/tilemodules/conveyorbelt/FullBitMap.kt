package com.cout970.magneticraft.systems.tilemodules.conveyorbelt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of

class FullBitMap(val current: IBitMap, val others: Map<BitmapLocation, IBitMap>) : IBitMap {

    override fun get(x: Int, y: Int): Boolean {
        if (x in 0..15 && y in 0..15) return current[x, y]
        for ((loc, map) in others) {
            if (loc.isInside(x, y)) {
                val pos = loc.fromLocalToExternal(x, y)
                return map[pos.xi, pos.yi]
            }
        }
        return false
    }

    override fun set(x: Int, y: Int, value: Boolean) {
        if (x in 0..15 && y in 0..15) {
            current[x, y] = value
            return
        }
        for ((loc, map) in others) {
            if (loc.isInside(x, y)) {
                val pos = loc.fromLocalToExternal(x, y)
                map[pos.xi, pos.yi] = value
                return
            }
        }
    }

    override fun mark(box: AABB) {
        mark(vec2Of(box.minX * 16.0, box.minZ * 16.0), vec2Of(box.maxX * 16.0, box.maxZ * 16.0))
    }

    override fun mark(start: IVector2, end: IVector2) {
        set(start, end, true)
    }

    override fun unmark(box: AABB) {
        unmark(vec2Of(box.minX * 16, box.minZ * 16), vec2Of(box.maxX * 16, box.maxZ * 16))
    }

    override fun unmark(start: IVector2, end: IVector2) {
        set(start, end, false)
    }

    fun set(start: IVector2, end: IVector2, value: Boolean) {

        val minX = Math.floor(start.x).toInt()
        val maxX = Math.ceil(end.x).toInt()

        val minY = Math.floor(start.y).toInt()
        val maxY = Math.ceil(end.y).toInt()

        for (i in minX until maxX) {
            for (j in minY until maxY) {
                this[i, j] = value
            }
        }
    }

    override fun test(box: AABB): Boolean {
        return test(vec2Of(box.minX, box.minZ) * 16, vec2Of(
            box.maxX, box.maxZ) * 16)
    }

    override fun test(start: IVector2, end: IVector2): Boolean {

        val minX = Math.floor(start.x).toInt()
        val maxX = Math.ceil(end.x).toInt()

        val minY = Math.floor(start.y).toInt()
        val maxY = Math.ceil(end.y).toInt()

        for (i in minX until maxX) {
            for (j in minY until maxY) {
                if (this[i, j]) return false
            }
        }

        return true
    }

    override fun clear() {
        current.clear()
        others.values.forEach { it.clear() }
    }

    override fun copy(): IBitMap {
        return FullBitMap(current.copy(), others.mapValues { it.value.copy() })
    }

    override fun toString(): String {
        return "FullBitMap(current=$current, others=$others)"
    }
}