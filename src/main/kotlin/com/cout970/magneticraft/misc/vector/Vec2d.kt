package com.cout970.magneticraft.misc.vector


/**
 * Created by cout970 on 14/05/2016.
 */

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(x: Number, y: Number) = Vec2d(x.toDouble(), y.toDouble())

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(x: Int, y: Int) = Vec2d(x.toDouble(), y.toDouble())

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(x: Float, y: Float) = Vec2d(x.toDouble(), y.toDouble())

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(x: Double, y: Double) = Vec2d(x, y)

@Suppress("NOTHING_TO_INLINE")
inline fun vec2Of(n: Number) = Vec2d(n.toDouble(), n.toDouble())

inline val Pair<Vec2d, Vec2d>.start get() = first
inline val Pair<Vec2d, Vec2d>.end get() = first + second
inline val Pair<Vec2d, Vec2d>.pos get() = first
inline val Pair<Vec2d, Vec2d>.size get() = second

@Suppress("NOTHING_TO_INLINE")
inline operator fun Pair<Vec2d, Vec2d>.contains(point: Vec2d): Boolean {
    return (point.x in (start.x..end.x)) && (point.y in (start.y..end.y))
}

@Suppress("NOTHING_TO_INLINE")
inline fun Pair<Vec2d, Vec2d>.offset(offset: Vec2d): Pair<Vec2d, Vec2d> {
    return first + offset to second
}

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
    @Suppress("NOTHING_TO_INLINE")
    inline fun withX(x: Number) = Vec2d(x.toDouble(), y)
    @Suppress("NOTHING_TO_INLINE")
    inline fun withY(y: Number) = Vec2d(x, y.toDouble())

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

    infix fun toPoint(other: Vec2d) = Pair(this, other - this)

    fun floor() = Vec2d(x.toInt(), y.toInt())

    fun transform(op: (Double) -> Double) = Vec2d(op(x), op(y))
}

