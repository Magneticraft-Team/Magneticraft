package com.cout970.magneticraft.util

import coffee.cypher.mcextlib.extensions.aabb.to
import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.vectors.plus
import coffee.cypher.mcextlib.extensions.vectors.transform
import com.cout970.loader.impl.util.rotateX
import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 19/08/2016.
 */

operator fun List<Multiblock.MultiblockLayer>.get(x: Int, y: Int, z: Int): IMultiblockComponent {
    return this[this.size - y - 1].components[z][x]
}

fun EnumFacing.rotatePoint(origin: BlockPos, point: BlockPos): BlockPos {
    val rel = point - origin
    val rot = when (this) {
        EnumFacing.DOWN -> return BlockPos(origin + BlockPos(Vec3d(rel).rotateX(-90.0)))
        EnumFacing.UP -> return BlockPos(origin + BlockPos(Vec3d(rel).rotateX(90.0)))
        EnumFacing.NORTH -> return point
        EnumFacing.SOUTH -> 180.0f
        EnumFacing.WEST -> 90.0f
        EnumFacing.EAST -> 360f - 90.0f
    }
    val pos2 = Vec3d(rel).rotateYaw(rot.toRadians())
    val pos3 = pos2.transform { Math.round(it).toDouble() }
    return BlockPos(origin + BlockPos(pos3))
}

fun EnumFacing.rotatePoint(origin: Vec3d, point: Vec3d): Vec3d {
    val rel = point - origin
    val rot = when (this) {
        EnumFacing.DOWN -> return origin + rel.rotateX(-90.0)
        EnumFacing.UP -> return origin + rel.rotateX(90.0)
        EnumFacing.NORTH -> return point
        EnumFacing.SOUTH -> 180.0f
        EnumFacing.WEST -> 90.0f
        EnumFacing.EAST -> -90.0f
    }
    return origin + rel.rotateYaw(rot.toRadians())
}

fun EnumFacing.rotateBox(origin: Vec3d, box: AxisAlignedBB): AxisAlignedBB {
    val min = Vec3d(box.minX, box.minY, box.minZ)
    val max = Vec3d(box.maxX, box.maxY, box.maxZ)
    return rotatePoint(origin, min) to rotatePoint(origin, max)
}