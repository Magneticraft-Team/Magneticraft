package com.cout970.magneticraft.misc.vector

import com.cout970.magneticraft.misc.toRadians
import com.cout970.magneticraft.misc.toRads
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumFacing.*
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/02/20.
 */

fun EnumFacing.getRelative(other: EnumFacing): EnumFacing = when (this) {
    EnumFacing.DOWN, EnumFacing.UP, EnumFacing.NORTH -> other
    EnumFacing.SOUTH -> other.safeRotateY().safeRotateY()
    EnumFacing.WEST -> other.safeRotateYCCW()
    EnumFacing.EAST -> other.safeRotateY()
}

fun EnumFacing.safeRotateYCCW(): EnumFacing {
    return when (this) {
        NORTH -> WEST
        EAST -> NORTH
        SOUTH -> EAST
        WEST -> SOUTH
        else -> this
    }
}

fun EnumFacing.safeRotateY(): EnumFacing {
    return when (this) {
        NORTH -> EAST
        EAST -> SOUTH
        SOUTH -> WEST
        WEST -> NORTH
        else -> this
    }
}

fun EnumFacing.rotatePoint(origin: BlockPos = BlockPos.ORIGIN, point: BlockPos): BlockPos {
    val rel = point - origin
    val rot = when (this) {
        DOWN -> return BlockPos(origin + BlockPos(Vec3d(rel).rotatePitch(-90.0f)))
        UP -> return BlockPos(origin + BlockPos(Vec3d(rel).rotatePitch(90.0f)))
        NORTH -> return point
        SOUTH -> 180.0f
        WEST -> 90.0f
        EAST -> 360f - 90.0f
    }
    val pos2 = Vec3d(rel).rotateYaw(rot.toRadians())
    val pos3 = pos2.transform { Math.round(it).toDouble() }
    return BlockPos(origin + BlockPos(pos3))
}

/**
 * The default value is associated to the NORTH direction
 */
fun EnumFacing.rotatePoint(origin: Vec3d, point: Vec3d): Vec3d {
    val rel = point - origin
    val rot = when (this) {
        DOWN -> return origin + rel.rotatePitch(90.toRads().toFloat())
        UP -> return origin + rel.rotatePitch(-90.toRads().toFloat())
        NORTH -> return point
        SOUTH -> 180.0f
        WEST -> 90.0f
        EAST -> -90.0f
    }
    return origin + rel.rotateYaw(rot.toRadians())
}

fun EnumFacing.rotateBox(origin: Vec3d, box: AxisAlignedBB): AxisAlignedBB {
    val min = Vec3d(box.minX, box.minY, box.minZ)
    val max = Vec3d(box.maxX, box.maxY, box.maxZ)
    return rotatePoint(origin, min) createAABBUsing rotatePoint(origin, max)
}

fun EnumFacing.isHorizontal() = this != UP && this != DOWN

fun EnumFacing.toBlockPos() = directionVec.toBlockPos()
fun EnumFacing.toVector3() = directionVec.toVec3d()

inline val EnumFacing.lowercaseName get() = name2