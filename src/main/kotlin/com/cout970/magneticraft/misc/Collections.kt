package com.cout970.magneticraft.misc

import com.cout970.magneticraft.systems.multiblocks.IMultiblockComponent
import com.cout970.magneticraft.systems.multiblocks.Multiblock
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

fun <T> flatten(vararg lists: List<T>): List<T> {
    val newList = mutableListOf<T>()
    for (list in lists) {
        for (element in list) {
            newList.add(element)
        }
    }
    return newList
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

fun encodeFlags(vararg flags: Boolean): Int {
    require(flags.size <= 32)
    var value = 0
    repeat(flags.size) {
        if (flags[it]) {
            value = value or (1 shl it)
        }
    }
    return value
}

fun decodeFlags(value: Int, limit: Int = 32): BooleanArray {
    val array = BooleanArray(limit)
    repeat(limit) {
        array[it] = (value ushr it) and 0x1 != 0
    }
    return array
}