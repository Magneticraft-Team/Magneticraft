@file:JvmName("BlockUtilities")

package com.cout970.magneticraft.misc.block

import com.cout970.magneticraft.AABB
import com.cout970.magneticraft.systems.blocks.CommonMethods
import net.minecraft.block.BlockState
import net.minecraft.state.IProperty
import net.minecraft.util.Direction

/**
 * Created by cout970 on 2017/02/20.
 */

val FULL_BLOCK_AABB = AABB(
    0.0, 0.0, 0.0,
    1.0, 1.0, 1.0
)

operator fun <T : Comparable<T>> BlockState.get(property: IProperty<T>): T? = if (property.isIn(this)) get(property) else null

fun <T : Comparable<T>> IProperty<T>.isIn(state: BlockState): Boolean = this in state.properties

fun BlockState.getFacing() = this[CommonMethods.PROPERTY_FACING]?.facing ?: Direction.DOWN

fun BlockState.getOrientation() = this[CommonMethods.PROPERTY_ORIENTATION]?.facing ?: Direction.NORTH

fun BlockState.getOrientationActive() = this[CommonMethods.PROPERTY_ORIENTATION_ACTIVE]?.facing ?: Direction.NORTH

fun BlockState.getOrientationCentered() = this[CommonMethods.PROPERTY_CENTER_ORIENTATION]?.facing ?: Direction.NORTH