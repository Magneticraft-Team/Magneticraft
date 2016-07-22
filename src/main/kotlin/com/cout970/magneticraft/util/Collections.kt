package com.cout970.magneticraft.util

import java.util.*

fun<T> List<T>.shuffled() = toMutableList().apply(Collections::shuffle)

inline infix fun<reified T> List<T>.with(other: List<T>): MutableList<T> = mutableListOf(*toTypedArray(), *other.toTypedArray())