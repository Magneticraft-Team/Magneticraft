package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */

data class Vec4d(val x: Double, val y: Double, val z: Double, val w: Double) {
    companion object {
        val ZERO = Vec4d(0, 0, 0, 0)
    }

    val lengthSquared = x * x + y * y + z * z + w * w

    val length = Math.sqrt(lengthSquared)

    constructor(x: Number, y: Number, z: Number, w: Number) : this(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

    fun getXf(): Float = x.toFloat()

    fun getYf(): Float = y.toFloat()

    fun getZf(): Float = z.toFloat()

    fun getWf(): Float = w.toFloat()

    fun getXi(): Int = x.toInt()

    fun getYi(): Int = y.toInt()

    fun getZi(): Int = z.toInt()

    fun getWi(): Int = w.toInt()

    fun xy() = Vec2d(x, y)

    fun xyz() = Vec3d(x, y, z)

    fun xz() = Vec2d(x, z)

    operator fun plus(v: Double) = Vec4d(v + x, v + y, v + z, v + w)

    operator fun plus(v: Vec4d) = Vec4d(v.x + x, v.y + y, v.z + z, v.w + w)

    operator fun minus(v: Double) = Vec4d(v - x, v - y, v - z, v - w)

    operator fun minus(v: Vec4d) = Vec4d(v.x - x, v.y - y, v.z - z, v.w - w)

    operator fun times(v: Vec4d) = Vec4d(v.x * x, v.y * y, v.z * z, v.w * w)

    operator fun times(v: Double) = Vec4d(v * x, v * y, v * z, v * w)

    operator fun div(v: Vec4d) = Vec4d(x / v.x, y / v.y, z / v.z, w / v.w)

    operator fun div(v: Double) = Vec4d(x / v, y / v, z / v, w / v)

    operator fun unaryMinus() = Vec4d(-x, -y, -z, -w)

    fun round() = Vec4d(x.toInt(), y.toInt(), z.toInt(), w.toInt())

    fun transform(op: (Double) -> Double) = Vec4d(op(x), op(y), op(z), op(w))
}