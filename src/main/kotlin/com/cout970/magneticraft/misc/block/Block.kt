@file:JvmName("BlockUtilities")

package com.cout970.magneticraft.misc.block

import com.cout970.magneticraft.systems.blocks.CommonMethods
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.util.EnumFacing

/**
 * Created by cout970 on 2017/02/20.
 */

operator fun <T : Comparable<T>> IBlockState.get(property: IProperty<T>): T? = if (property.isIn(this)) getValue(property) else null

fun <T : Comparable<T>> IProperty<T>.isIn(state: IBlockState): Boolean = this in state.properties

fun IBlockState.getFacing() = this[CommonMethods.PROPERTY_FACING]?.facing ?: EnumFacing.DOWN

fun IBlockState.getOrientation() = this[CommonMethods.PROPERTY_ORIENTATION]?.facing ?: EnumFacing.NORTH

fun IBlockState.getOrientationActive() = this[CommonMethods.PROPERTY_ORIENTATION_ACTIVE]?.facing ?: EnumFacing.NORTH

fun IBlockState.getOrientationCentered() = this[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.facing ?: EnumFacing.NORTH