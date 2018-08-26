package com.cout970.magneticraft.util

import com.cout970.magneticraft.multiblock.core.IMultiblockComponent
import com.cout970.magneticraft.multiblock.core.Multiblock
import java.util.*


/**
 * Created by cout970 on 19/08/2016.
 */

operator fun List<Multiblock.MultiblockLayer>.get(x: Int, y: Int, z: Int): IMultiblockComponent {
    return this[this.size - y - 1].components[z][x]
}

fun <T> MutableList<T>.setAll(it: Iterable<T>) {
    clear()
    addAll(it)
}

fun <T> List<T>.shuffled() = toMutableList().apply(Collections::shuffle)

inline infix fun <reified T> List<T>.with(other: List<T>): MutableList<T> = mutableListOf(*toTypedArray(),
    *other.toTypedArray())

fun <T> List<T>.rest(): List<T> {
    if (isEmpty() || size == 1) return emptyList()
    return drop(1)
}

fun runningAvg(previousAverage: Float, currentNumber: Float, index: Int): Float {
    // avg = (avg' * (n-1) + x) / n
    return (previousAverage * (index - 1) + currentNumber) / index
}