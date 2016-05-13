package com.cout970.magneticraft.util

import java.util.*

fun<T> List<T>.shuffled() = toMutableList().apply(Collections::shuffle)