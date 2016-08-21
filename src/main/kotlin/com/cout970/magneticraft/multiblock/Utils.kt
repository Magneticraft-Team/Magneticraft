package com.cout970.magneticraft.multiblock

import coffee.cypher.mcextlib.extensions.vectors.minus
import coffee.cypher.mcextlib.extensions.vectors.plus
import coffee.cypher.mcextlib.extensions.vectors.round
import com.cout970.loader.impl.util.rotateX
import com.cout970.magneticraft.util.toRadians
import net.minecraft.util.EnumFacing
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
        EnumFacing.WEST -> -90.0f
        EnumFacing.EAST -> 90.0f
    }
    return BlockPos(origin + BlockPos(Vec3d(rel).rotateYaw(rot.toRadians()).round()))
}