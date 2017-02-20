package com.cout970.magneticraft.misc.gui

import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 08/07/2016.
 */
data class Box(val pos: Vec2d, val size: Vec2d) {

    val start: Vec2d get() = pos
    val end: Vec2d get() = pos + size

    operator fun contains(point: Vec2d): Boolean = (point.x in (start.x..end.x)) && (point.y in (start.y..end.y))

}