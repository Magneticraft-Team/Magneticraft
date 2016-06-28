package com.cout970.magneticraft.util.vector

/**
 * Created by cout970 on 14/05/2016.
 */

data class Vec2d(val x: Double, val y: Double) {
    companion object {
        val ZERO = Vec2d(0.0, 0.0)
    }

    val lengthSquared = x * x + y * y

    val length = Math.sqrt(lengthSquared)

    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    fun getXf(): Float = x.toFloat()

    fun getYf(): Float = y.toFloat()

    fun getXi(): Int = x.toInt()

    fun getYi(): Int = y.toInt()

    fun toPair(): Pair<Double, Double> = x to y

    fun yx() = Vec2d(y, x)

    operator fun plus(v: Double) = Vec2d(v + x, v + y)

    operator fun plus(v: Vec2d) = Vec2d(v.x + x, v.y + y)

    operator fun minus(v: Double) = Vec2d(v - x, v - y)

    operator fun minus(v: Vec2d) = Vec2d(v.x - x, v.y - y)

    operator fun times(v: Vec2d) = Vec2d(v.x * x, v.y * y)

    operator fun times(v: Double) = Vec2d(v * x, v * y)

    operator fun div(v: Vec2d) = Vec2d(x / v.x, y / v.y)

    operator fun div(v: Double) = Vec2d(x / v, y / v)

    operator fun unaryMinus() = Vec2d(-x, -y)

    fun round() = Vec2d(x.toInt(), y.toInt())

    fun transform(op: (Double) -> Double) = Vec2d(op(x), op(y))
}