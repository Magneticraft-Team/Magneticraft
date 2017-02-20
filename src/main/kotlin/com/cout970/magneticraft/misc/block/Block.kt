package com.cout970.magneticraft.misc.block

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState

/**
 * Created by cout970 on 2017/02/20.
 */

operator fun <T : Comparable<T>> IBlockState.get(property: IProperty<T>): T = getValue(property)

fun <T : Comparable<T>> IProperty<T>.isIn(state: IBlockState): Boolean = this in state.properties