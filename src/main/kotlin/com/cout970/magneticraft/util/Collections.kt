package com.cout970.magneticraft.util

import com.cout970.magneticraft.multiblock.IMultiblockComponent
import com.cout970.magneticraft.multiblock.Multiblock
import java.util.*

fun<T> List<T>.shuffled() = toMutableList().apply(Collections::shuffle)

inline infix fun<reified T> List<T>.with(other: List<T>): MutableList<T> = mutableListOf(*toTypedArray(), *other.toTypedArray())
/**
 * Created by cout970 on 19/08/2016.
 */

operator fun List<Multiblock.MultiblockLayer>.get(x: Int, y: Int, z: Int): IMultiblockComponent {
    return this[this.size - y - 1].components[z][x]
}