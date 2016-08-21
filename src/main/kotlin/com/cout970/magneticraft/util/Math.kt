package com.cout970.magneticraft.util

import coffee.cypher.mcextlib.extensions.vectors.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

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
    if (s >= 0.0 && s <= 1.0) {
        return true
    }
    return false
}

private fun norm2(v: Vec3d): Double = v.x * v.x + v.y * v.y + v.z * v.z

operator fun BlockPos.plus(dir: EnumFacing) = this.offset(dir)!!
operator fun BlockPos.plus(dir: BlockPos) = this.add(dir)!!
operator fun Vec3i.plus(dir: EnumFacing) = this.toBlockPos().offset(dir)!!