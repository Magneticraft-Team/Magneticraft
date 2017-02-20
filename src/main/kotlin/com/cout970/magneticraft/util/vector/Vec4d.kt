package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */
@Suppress("unused")
data class Vec4d(val x: Double, val y: Double, val z: Double, val w: Double) {

    companion object {
        val ZERO = Vec4d(0, 0, 0, 0)
    }

    val lengthSquared get () =  x * x + y * y + z * z + w * w

    val length get () = Math.sqrt(lengthSquared)

    constructor(x: Number, y: Number, z: Number, w: Number) : this(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())

    val xf: Float get() = x.toFloat()

    val yf: Float get() = y.toFloat()

    val zf: Float get() = z.toFloat()

    val wf: Float get() = w.toFloat()

    val xi: Int get() = x.toInt()

    val yi: Int get() = y.toInt()

    val zi: Int get() = z.toInt()

    val wi: Int get() = w.toInt()

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