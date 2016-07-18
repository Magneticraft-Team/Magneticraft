package com.cout970.magneticraft.util.vector

import net.minecraft.util.math.Vec3d

/**
 * Created by cout970 on 14/05/2016.
 */
fun Vec3d(x: Number, y: Number, z: Number) = net.minecraft.util.math.Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

// getters

val Vec3d.xf: Float get() = xCoord.toFloat()

val Vec3d.yf: Float get() = yCoord.toFloat()

val Vec3d.zf: Float get() = zCoord.toFloat()

val Vec3d.xi: Int get() = xCoord.toInt()

val Vec3d.yi: Int get() = yCoord.toInt()

val Vec3d.zi: Int get() = zCoord.toInt()

// utilities

val Vec3d.xy: Vec2d get() = Vec2d(xCoord, yCoord)

val Vec3d.yz: Vec2d get() = Vec2d(yCoord, zCoord)

val Vec3d.xz: Vec2d get() = Vec2d(xCoord, zCoord)