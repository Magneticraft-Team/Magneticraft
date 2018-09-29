@file:Suppress("unused")

package com.cout970.magneticraft.misc.vector

import com.cout970.magneticraft.IVector3
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.model.TRSRTransformation
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

/**
 * Created by cout970 on 14/05/2016.
 */
fun vec3Of(x: Number, y: Number, z: Number): IVector3 = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

fun vec3Of(x: Double, y: Double, z: Double): IVector3 = Vec3d(x, y, z)
fun vec3Of(x: Float, y: Float, z: Float): IVector3 = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
fun vec3Of(x: Int, y: Int, z: Int): IVector3 = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

fun vec3Of(n: Number): IVector3 = Vec3d(n.toDouble(), n.toDouble(), n.toDouble())

// getters

inline val Vec3d.xf: Float get() = x.toFloat()
inline val Vec3d.yf: Float get() = y.toFloat()
inline val Vec3d.zf: Float get() = z.toFloat()

inline val Vec3d.xi: Int get() = x.toInt()
inline val Vec3d.yi: Int get() = y.toInt()
inline val Vec3d.zi: Int get() = z.toInt()

inline val Vec3d.xd: Double get() = x
inline val Vec3d.yd: Double get() = y
inline val Vec3d.zd: Double get() = z

// utilities

val Vec3d.xy: Vec2d get() = Vec2d(x, y)
val Vec3d.yz: Vec2d get() = Vec2d(y, z)
val Vec3d.xz: Vec2d get() = Vec2d(x, z)

val Vec3d.lengthSqr: Double get() = xd * xd + yd * yd + zd * zd
val Vec3d.length: Double get() = Math.sqrt(lengthSqr)

operator fun Vec3d.minus(other: Vec3d) = Vec3d(xd - other.xd, yd - other.yd, zd - other.zd)
operator fun Vec3d.plus(other: Vec3d) = Vec3d(xd + other.xd, yd + other.yd, zd + other.zd)
operator fun Vec3d.times(other: Vec3d) = Vec3d(xd * other.xd, yd * other.yd, zd * other.zd)
operator fun Vec3d.div(other: Vec3d) = Vec3d(xd / other.xd, yd / other.yd, zd / other.zd)

operator fun Vec3d.minus(other: Number) = Vec3d(x - other.toDouble(), y - other.toDouble(), z - other.toDouble())
operator fun Vec3d.plus(other: Number) = Vec3d(x + other.toDouble(), y + other.toDouble(), z + other.toDouble())
operator fun Vec3d.times(other: Number) = Vec3d(x * other.toDouble(), y * other.toDouble(), z * other.toDouble())
operator fun Vec3d.div(other: Number) = Vec3d(x / other.toDouble(), y / other.toDouble(), z / other.toDouble())

operator fun Vec3d.unaryMinus() = Vec3d(-xd, -yd, -zd)

operator fun Vec3d.component1() = x
operator fun Vec3d.component2() = y
operator fun Vec3d.component3() = z

fun Vec3d.transform(op: (Double) -> Double) = Vec3d(op(xd), op(yd), op(zd))


fun Vec3d.rotate(other: Vec3d): Vec3d {
    val quat = TRSRTransformation.quatFromXYZ(xf, yf, zf)
    val mat = Matrix4f()
    mat.setIdentity()
    mat.set(quat)
    val result = Vector3f(other.xf, other.yf, other.zf)
    mat.transform(result)
    return vec3Of(result.x, result.y, result.z)
}