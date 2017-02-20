@file:Suppress("unused")

package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */
fun vec3Of(x: Number, y: Number, z: Number) = net.minecraft.util.math.Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

// getters

val Vec3d.xf: Float get() = xCoord.toFloat()
val Vec3d.yf: Float get() = yCoord.toFloat()
val Vec3d.zf: Float get() = zCoord.toFloat()

val Vec3d.xi: Int get() = xCoord.toInt()
val Vec3d.yi: Int get() = yCoord.toInt()
val Vec3d.zi: Int get() = zCoord.toInt()

val Vec3d.xd: Double get() = xCoord
val Vec3d.yd: Double get() = yCoord
val Vec3d.zd: Double get() = zCoord

// utilities

val Vec3d.xy: Vec2d get() = Vec2d(xCoord, yCoord)
val Vec3d.yz: Vec2d get() = Vec2d(yCoord, zCoord)
val Vec3d.xz: Vec2d get() = Vec2d(xCoord, zCoord)

val Vec3d.lengthSqr: Double get() = xd * xd + yd * yd + zd * zd
val Vec3d.length: Double get() = Math.sqrt(lengthSqr)

operator fun Vec3d.minus(other: Vec3d) = Vec3d(xd - other.xd, yd - other.yd, zd - other.zd)
operator fun Vec3d.plus(other: Vec3d) = Vec3d(xd + other.xd, yd + other.yd, zd + other.zd)
operator fun Vec3d.times(other: Vec3d) = Vec3d(xd * other.xd, yd * other.yd, zd * other.zd)
operator fun Vec3d.div(other: Vec3d) = Vec3d(xd / other.xd, yd / other.yd, zd / other.zd)

operator fun Vec3d.minus(other: Number) = Vec3d(xCoord - other.toDouble(), yCoord - other.toDouble(), zCoord - other.toDouble())
operator fun Vec3d.plus(other: Number) = Vec3d(xCoord + other.toDouble(), yCoord + other.toDouble(), zCoord + other.toDouble())
operator fun Vec3d.times(other: Number) = Vec3d(xCoord * other.toDouble(), yCoord * other.toDouble(), zCoord * other.toDouble())
operator fun Vec3d.div(other: Number) = Vec3d(xCoord / other.toDouble(), yCoord / other.toDouble(), zCoord / other.toDouble())

operator fun Vec3d.unaryMinus() = Vec3d(-xd, -yd, -zd)

operator fun Vec3d.component1() = xCoord
operator fun Vec3d.component2() = yCoord
operator fun Vec3d.component3() = zCoord

fun Vec3d.transform(op: (Double) -> Double) = Vec3d(op(xd), op(yd), op(zd))
