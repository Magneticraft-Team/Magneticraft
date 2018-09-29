package com.cout970.magneticraft.systems.tilemodules.conveyorbelt

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.IVector2

interface IBitMap {
    operator fun get(x: Int, y: Int): Boolean

    operator fun set(x: Int, y: Int, value: Boolean)

    fun mark(box: AABB)
    fun mark(start: IVector2, end: IVector2)

    fun unmark(box: AABB)
    fun unmark(start: IVector2, end: IVector2)

    fun test(box: AABB): Boolean

    fun test(start: IVector2, end: IVector2): Boolean

    fun clear()
    fun copy(): IBitMap
}