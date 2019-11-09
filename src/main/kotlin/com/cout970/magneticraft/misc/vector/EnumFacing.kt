package com.cout970.magneticraft.misc.vector

import com.cout970.magneticraft.misc.toRadians
import com.cout970.magneticraft.misc.toRads
import net.minecraft.util.Direction
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 2017/02/20.
 */

fun Direction.getRelative(other: Direction): Direction = when (this) {
    Direction.DOWN, Direction.UP, Direction.NORTH -> other
    Direction.SOUTH -> other.safeRotateY().safeRotateY()
    Direction.WEST -> other.safeRotateYCCW()
    Direction.EAST -> other.safeRotateY()
}

fun Direction.safeRotateYCCW(): Direction {
    return when (this) {
        Direction.NORTH -> Direction.WEST
        Direction.EAST -> Direction.NORTH
        Direction.SOUTH -> Direction.EAST
        Direction.WEST -> Direction.SOUTH
        else -> this
    }
}

fun Direction.safeRotateY(): Direction {
    return when (this) {
        Direction.NORTH -> Direction.EAST
        Direction.EAST -> Direction.SOUTH
        Direction.SOUTH -> Direction.WEST
        Direction.WEST -> Direction.NORTH
        else -> this
    }
}

fun Direction.rotatePoint(origin: BlockPos = BlockPos.ZERO, point: BlockPos): BlockPos {
    val rel = point - origin
    val rot = when (this) {
        Direction.DOWN -> return BlockPos.ZERO + BlockPos(Vec3d(rel).rotatePitch(-90.0f))
        Direction.UP -> return BlockPos.ZERO + BlockPos(Vec3d(rel).rotatePitch(90.0f))
        Direction.NORTH -> return point
        Direction.SOUTH -> 180.0f
        Direction.WEST -> 90.0f
        Direction.EAST -> 360f - 90.0f
    }
    val pos2 = Vec3d(rel).rotateYaw(rot.toRadians())
    val pos3 = pos2.transform { Math.round(it).toDouble() }
    return BlockPos.ZERO + BlockPos(pos3)
}

/**
 * The default value is associated to the NORTH direction
 */
fun Direction.rotatePoint(origin: Vec3d, point: Vec3d): Vec3d {
    val rel = point - origin
    val rot = when (this) {
        Direction.DOWN -> return origin + rel.rotatePitch(90.toRads().toFloat())
        Direction.UP -> return origin + rel.rotatePitch(-90.toRads().toFloat())
        Direction.NORTH -> return point
        Direction.SOUTH -> 180.0f
        Direction.WEST -> 90.0f
        Direction.EAST -> -90.0f
    }
    return origin + rel.rotateYaw(rot.toRadians())
}

fun Direction.rotateBox(origin: Vec3d, box: AxisAlignedBB): AxisAlignedBB {
    val min = Vec3d(box.minX, box.minY, box.minZ)
    val max = Vec3d(box.maxX, box.maxY, box.maxZ)
    return rotatePoint(origin, min) createAABBUsing rotatePoint(origin, max)
}

fun Direction.isHorizontal() = this != Direction.UP && this != Direction.DOWN

fun Direction.toBlockPos() = directionVec.toBlockPos()
fun Direction.toVector3() = directionVec.toVec3d()

inline val Direction.lowercaseName get() = name2