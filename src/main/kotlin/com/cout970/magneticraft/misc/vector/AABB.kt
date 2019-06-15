package com.cout970.magneticraft.misc.vector

import com.cout970.magneticraft.IVector3
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i


/**
 * Created by cout970 on 2017/02/20.
 */

operator fun AxisAlignedBB.plus(offsetVector: Vec3i): AxisAlignedBB = offset(offsetVector)

operator fun AxisAlignedBB.plus(offsetVector: Vec3d): AxisAlignedBB = offset(offsetVector)

operator fun AxisAlignedBB.minus(offsetVector: Vec3i): AxisAlignedBB = offset(-offsetVector)

operator fun AxisAlignedBB.minus(offsetVector: Vec3d): AxisAlignedBB = offset(-offsetVector)

// Offset overloads for vectors

fun AxisAlignedBB.offset(offsetVector: Vec3i): AxisAlignedBB = offset(offsetVector.toBlockPos())

//Box construction

infix fun BlockPos.createAABBUsing(other: BlockPos) = AxisAlignedBB(this.toBlockPos(), other.toBlockPos())
infix fun Vec3d.createAABBUsing(other: Vec3d) = AxisAlignedBB(xd, yd, zd, other.xd, other.yd, other.zd)

//Box deconstruction

operator fun AxisAlignedBB.component1() = Vec3d(minX, minY, minZ)
operator fun AxisAlignedBB.component2() = Vec3d(maxX, maxY, maxZ)


fun AxisAlignedBB.cut(other: AxisAlignedBB): AxisAlignedBB? {
    if (!this.intersects(other)) return null
    return AxisAlignedBB(
        Math.max(minX, other.minX), Math.max(minY, other.minY), Math.max(minZ, other.minZ),
        Math.min(maxX, other.maxX), Math.min(maxY, other.maxY), Math.min(maxZ, other.maxZ))
}

fun AxisAlignedBB.scale(scale: Double) = AxisAlignedBB(
    minX * scale, minY * scale, minZ * scale,
    maxX * scale, maxY * scale, maxZ * scale
)

infix fun Double.doubleEquals(other: Double): Boolean {
    return Math.abs(this - other) < 1e-6
}

fun AxisAlignedBB.containsPoint(vec: Vec3d): Boolean {
    return vec.x >= this.minX && vec.x <= this.maxX &&
        vec.y >= this.minY && vec.y <= this.maxY &&
        vec.z >= this.minZ && vec.z <= this.maxZ
}

fun AxisAlignedBB.isHitBy(v: IVector3): Boolean {
    if (maxY doubleEquals v.y || minY doubleEquals v.y) {
        if (v.x in minX..maxX) {
            if (v.z in minZ..maxZ) {
                return true
            }
        }
    }
    if (maxX doubleEquals v.x || minX doubleEquals v.x) {
        if (v.y in minY..maxY) {
            if (v.z in minZ..maxZ) {
                return true
            }
        }
    }
    if (maxZ doubleEquals v.z || minZ doubleEquals v.z) {
        if (v.x in minX..maxX) {
            if (v.y in minY..maxY) {
                return true
            }
        }
    }
    return false
}

val EMPTY_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)