@file:Suppress("unused")

package com.cout970.magneticraft.util.vector

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2017/02/20.
 */

val BlockPos.xf: Float get() = x.toFloat()
val BlockPos.yf: Float get() = y.toFloat()
val BlockPos.zf: Float get() = z.toFloat()

val BlockPos.xi: Int get() = x
val BlockPos.yi: Int get() = y
val BlockPos.zi: Int get() = z

val BlockPos.xd: Double get() = x.toDouble()
val BlockPos.yd: Double get() = y.toDouble()
val BlockPos.zd: Double get() = z.toDouble()

val BlockPos.lengthSqr: Double get() = xd * xd + yd * yd + zd * zd
val BlockPos.length: Double get() = Math.sqrt(lengthSqr)

operator fun BlockPos.plus(dir: EnumFacing) = this.offset(dir)!!
operator fun BlockPos.minus(dir: EnumFacing) = this.offset(dir.opposite)!!


operator fun BlockPos.minus(other: BlockPos) = BlockPos(x - other.x, y - other.y, z - other.z)
operator fun BlockPos.plus(other: BlockPos) = BlockPos(x + other.x, y + other.y, z + other.z)
operator fun BlockPos.times(other: BlockPos) = BlockPos(x * other.x, y * other.y, z * other.z)
operator fun BlockPos.div(other: BlockPos) = BlockPos(x / other.x, y / other.y, z / other.z)
operator fun BlockPos.unaryMinus() = BlockPos(-xd, -yd, -zd)

operator fun BlockPos.component1() = x
operator fun BlockPos.component2() = y
operator fun BlockPos.component3() = z