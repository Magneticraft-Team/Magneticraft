@file:Suppress("unused")

package com.cout970.magneticraft.util.vector

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 2017/02/20.
 */

val Vec3i.xf: Float get() = x.toFloat()
val Vec3i.yf: Float get() = y.toFloat()
val Vec3i.zf: Float get() = z.toFloat()

val Vec3i.xi: Int get() = x
val Vec3i.yi: Int get() = y
val Vec3i.zi: Int get() = z

val Vec3i.xd: Double get() = x.toDouble()
val Vec3i.yd: Double get() = y.toDouble()
val Vec3i.zd: Double get() = z.toDouble()

val Vec3i.lengthSqr: Double get() = xd * xd + yd * yd + zd * zd
val Vec3i.length: Double get() = Math.sqrt(lengthSqr)

operator fun Vec3i.plus(dir: EnumFacing) = toBlockPos().offset(dir)!!
operator fun Vec3i.minus(dir: EnumFacing) = toBlockPos().offset(dir.opposite)!!

fun Vec3i.toBlockPos() = if (this is BlockPos) this else BlockPos(x, y, z)
fun Vec3d.toBlockPos() = BlockPos(xi, yi, zi)
fun Vec3i.toVec3d() = Vec3d(xd, yd, zd)

operator fun Vec3i.minus(other: Vec3i) = BlockPos(x - other.x, y - other.y, z - other.z)
operator fun Vec3i.plus(other: Vec3i) = BlockPos(x + other.x, y + other.y, z + other.z)
operator fun Vec3i.times(other: Vec3i) = BlockPos(x * other.x, y * other.y, z * other.z)
operator fun Vec3i.div(other: Vec3i) = BlockPos(x / other.x, y / other.y, z / other.z)

operator fun Vec3i.minus(other: Number) = BlockPos(x - other.toInt(), y - other.toInt(), z - other.toInt())
operator fun Vec3i.plus(other: Number) = BlockPos(x + other.toInt(), y + other.toInt(), z + other.toInt())
operator fun Vec3i.times(other: Number) = BlockPos(x * other.toInt(), y * other.toInt(), z * other.toInt())
operator fun Vec3i.div(other: Number) = BlockPos(x / other.toInt(), y / other.toInt(), z / other.toInt())

operator fun Vec3i.unaryMinus() = BlockPos(-xd, -yd, -zd)

operator fun Vec3i.component1() = x
operator fun Vec3i.component2() = y
operator fun Vec3i.component3() = z