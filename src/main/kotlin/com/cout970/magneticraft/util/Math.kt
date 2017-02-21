@file:Suppress("unused")

package com.cout970.magneticraft.util


import com.cout970.magneticraft.util.vector.minus
import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.util.math.Vec3d

infix fun Int.roundTo(factor: Int) = (this / factor) * factor

infix fun Long.roundTo(factor: Long) = (this / factor) * factor

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()

fun clamp(value: Double, max: Double, min: Double) = Math.max(Math.min(max, value), min)

fun hasIntersection(aFirst: Vec3d, aSecond: Vec3d, bFirst: Vec3d, bSecond: Vec3d): Boolean {
    val da = aSecond - aFirst
    val db = bSecond - bFirst
    val dc = bFirst - aFirst

    if (dc.dotProduct(da.crossProduct(db)) != 0.0) // lines are not coplanar
        return false

    val s = dc.crossProduct(db).dotProduct(da.crossProduct(db)) / norm2(da.crossProduct(db))
    if (s in 0.0..1.0) {
        return true
    }
    return false
}

private fun norm2(v: Vec3d): Double = v.xd * v.xd + v.yd * v.yd + v.zd * v.zd

fun interpolate(v: Double, min: Double, max: Double): Double {
    if (v < min) return 0.0
    if (v > max) return 1.0
    return (v - min) / (max - min)
}