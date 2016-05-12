package com.cout970.magneticraft.util

infix fun Int.roundTo(factor: Int) = (this / factor) * factor

infix fun Long.roundTo(factor: Long) = (this / factor) * factor