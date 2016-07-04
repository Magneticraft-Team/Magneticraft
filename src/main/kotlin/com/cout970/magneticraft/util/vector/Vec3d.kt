package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */
fun Vec3d(x: Number, y: Number, z: Number) = net.minecraft.util.math.Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

// getters

fun Vec3d.getXf(): Float = xCoord.toFloat()

fun Vec3d.getYf(): Float = yCoord.toFloat()

fun Vec3d.getZf(): Float = zCoord.toFloat()

fun Vec3d.getXi(): Int = xCoord.toInt()

fun Vec3d.getYi(): Int = yCoord.toInt()

fun Vec3d.getZi(): Int = zCoord.toInt()

// utilities

fun Vec3d.xy() = Vec2d(xCoord, yCoord)

fun Vec3d.yz() = Vec2d(yCoord, zCoord)

fun Vec3d.xz() = Vec2d(xCoord, zCoord)