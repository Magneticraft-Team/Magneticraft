package com.cout970.magneticraft.util

import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.vectors.x
import coffee.cypher.mcextlib.extensions.vectors.y
import coffee.cypher.mcextlib.extensions.vectors.z
import net.minecraft.util.math.Vec3d

infix fun Int.roundTo(factor: Int) = (this / factor) * factor

infix fun Long.roundTo(factor: Long) = (this / factor) * factor

fun Float.toRadians() = Math.toRadians(this.toDouble()).toFloat()

fun hasIntersection(aFirst: Vec3d, aSecond: Vec3d, bFirst: Vec3d, bSecond: Vec3d): Boolean {
    val da = aSecond - aFirst
    val db = bSecond - bFirst
    val dc = bFirst - aFirst

    if (dc.dotProduct(da.crossProduct(db)) != 0.0) // lines are not coplanar
        return false

    val s = dc.crossProduct(db).dotProduct(da.crossProduct(db)) / norm2(da.crossProduct(db))
    if (s >= 0.0 && s <= 1.0) {
        return true
    }
    return false
}

private fun norm2(v: Vec3d): Double = v.x * v.x + v.y * v.y + v.z * v.z