package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

/**
 * Created by cout970 on 2017/02/20.
 */

operator fun AxisAlignedBB.plus(offsetVector: Vec3i) = offset(offsetVector)

operator fun AxisAlignedBB.plus(offsetVector: Vec3d) = offset(offsetVector)

operator fun AxisAlignedBB.minus(offsetVector: Vec3i) = offset(-offsetVector)

operator fun AxisAlignedBB.minus(offsetVector: Vec3d) = offset(-offsetVector)

//Offset overloads for vectors

fun AxisAlignedBB.offset(offsetVector: Vec3i) = offset(offsetVector.toBlockPos())!!

fun AxisAlignedBB.offset(offsetVector: Vec3d) = offset(offsetVector.xd, offsetVector.yd, offsetVector.zd)!!

//Box construction

infix fun Vec3i.toAABBWith(other: Vec3i) = AxisAlignedBB(this.toBlockPos(), other.toBlockPos())
infix fun BlockPos.toAABBWith(other: BlockPos) = AxisAlignedBB(this.toBlockPos(), other.toBlockPos())
infix fun Vec3d.toAABBWith(other: Vec3d) = AxisAlignedBB(xd, yd, zd, other.xd, other.yd, other.zd)

//Box deconstruction

operator fun AxisAlignedBB.component1() = Vec3d(minX, minY, minZ)

operator fun AxisAlignedBB.component2() = Vec3d(maxX, maxY, maxZ)


fun AxisAlignedBB.cut(other: AxisAlignedBB): AxisAlignedBB? {
    if (!this.intersectsWith(other)) return null
    return AxisAlignedBB(
            Math.max(minX, other.minX), Math.max(minY, other.minY), Math.max(minZ, other.minZ),
            Math.min(maxX, other.maxX), Math.min(maxY, other.maxY), Math.min(maxZ, other.maxZ))
}

val EMPTY_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)