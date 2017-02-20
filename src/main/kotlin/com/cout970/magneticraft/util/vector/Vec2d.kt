package com.cout970.magneticraft.util.vector

import com.cout970.magneticraft.misc.gui.Box

/**
 * Created by cout970 on 14/05/2016.
 */

@Suppress("unused")
data class Vec2d(val x: Double, val y: Double) {

    companion object {
        val ZERO = Vec2d(0.0, 0.0)
    }

    val lengthSquared get() = x * x + y * y

    val length get() = Math.sqrt(lengthSquared)

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())
    constructor(x: Number) : this(x.toDouble(), x.toDouble())

    val xf: Float get() = x.toFloat()
    val yf: Float get() = y.toFloat()

    val xi: Int get() = x.toInt()
    val yi: Int get() = y.toInt()

    fun toPair(): Pair<Double, Double> = x to y

    fun swap() = Vec2d(y, x)

    operator fun plus(v: Number) = Vec2d(v.toDouble() + x, v.toDouble() + y)

    operator fun plus(v: Vec2d) = Vec2d(v.x + x, v.y + y)

    operator fun minus(v: Number) = Vec2d(x - v.toDouble(), y - v.toDouble())

    operator fun minus(v: Vec2d) = Vec2d(x - v.x, y - v.y)

    operator fun times(v: Vec2d) = Vec2d(v.x * x, v.y * y)

    operator fun times(v: Number) = Vec2d(v.toDouble() * x, v.toDouble() * y)

    operator fun div(v: Vec2d) = Vec2d(x / v.x, y / v.y)

    operator fun div(v: Number) = Vec2d(x / v.toDouble(), y / v.toDouble())

    operator fun unaryMinus() = Vec2d(-x, -y)

    fun center() = Vec2d(x / 2, y / 2)

    fun xCenter() = Vec2d(x / 2, y)

    fun yCenter() = Vec2d(x, y / 2)

    infix fun centeredAt(pos: Vec2d) = pos - center()

    fun centeredAt(x: Number, y: Number) = Vec2d(x, y) - center()

    infix fun to(other: Vec2d): Box = Box(this, other - this)

    fun floor() = Vec2d(x.toInt(), y.toInt())

    fun transform(op: (Double) -> Double) = Vec2d(op(x), op(y))
}

