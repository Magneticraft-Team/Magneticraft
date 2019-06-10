package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.api.core.ITileRef

class TimeCache<T>(val tile: ITileRef, val interval: Int, val getter: () -> T) {

    var time = 0L
    var cache: T? = null

    operator fun invoke(): T {
        val now = tile.world?.totalWorldTime ?: 0L

        if (now > interval + time || cache == null) {
            time = now
            cache = getter()
        }

        return cache!!
    }

    fun clear() {
        cache = null
        time = 0L
    }
}