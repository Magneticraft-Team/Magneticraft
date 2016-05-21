package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */


// constructors

fun Vec3d(): Vec3d = net.minecraft.util.math.Vec3d(0.0, 0.0, 0.0)

fun Vec3d(x: Float, y: Float, z: Float): Vec3d = net.minecraft.util.math.Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

fun Vec3d(x: Int, y: Int, z: Int): Vec3d = net.minecraft.util.math.Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

// getters

fun Vec3d.getX(): Double = xCoord.toDouble()

fun Vec3d.getY(): Double = yCoord.toDouble()

fun Vec3d.getZ(): Double = zCoord.toDouble()

fun Vec3d.getXf(): Float = xCoord.toFloat()

fun Vec3d.getYf(): Float = yCoord.toFloat()

fun Vec3d.getZf(): Float = zCoord.toFloat()

fun Vec3d.getXi(): Int = xCoord.toInt()

fun Vec3d.getYi(): Int = yCoord.toInt()

fun Vec3d.getZi(): Int = zCoord.toInt()

// setters

fun Vec3d.setX(xCoord: Double) = Vec3d(xCoord, yCoord, zCoord)

fun Vec3d.setY(yCoord: Double) = Vec3d(xCoord, yCoord, zCoord)

fun Vec3d.setZ(zCoord: Double) = Vec3d(xCoord, yCoord, zCoord)

// utilities

fun Vec3d.xy() = Vec2d(xCoord, yCoord)

fun Vec3d.yz() = Vec2d(yCoord, zCoord)

fun Vec3d.xz() = Vec2d(xCoord, zCoord)