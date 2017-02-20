@file:Suppress("unused")

package com.cout970.magneticraft.util

/**
 * Created by cout970 on 2016/10/01.
 */

fun Int.splitRange() = this..(this + 3)

fun Int.split(index: Int): Byte {
    return when (index) {
        0 -> (this and 0xFF).toByte()
        1 -> (this ushr 8 and 0xFF).toByte()
        2 -> (this ushr 16 and 0xFF).toByte()
        3 -> (this ushr 24 and 0xFF).toByte()
        else -> 0.toByte()
    }
}

fun Long.split(index: Int): Byte {
    return when (index) {
        0 -> (this and 0xFF).toByte()
        1 -> (this ushr 8 and 0xFF).toByte()
        2 -> (this ushr 16 and 0xFF).toByte()
        3 -> (this ushr 24 and 0xFF).toByte()
        4 -> (this ushr 32 and 0xFF).toByte()
        5 -> (this ushr 40 and 0xFF).toByte()
        6 -> (this ushr 48 and 0xFF).toByte()
        7 -> (this ushr 56 and 0xFF).toByte()
        else -> 0.toByte()
    }
}

fun Int.splitSet(index: Int, byte: Byte): Int {
    return when (index) {
        0 -> (0xFFFFFF00.toInt() and this) or byte.toInt()
        1 -> (0xFFFF00FF.toInt() and this) or (byte.toInt() shl 8)
        2 -> (0xFF00FFFF.toInt() and this) or (byte.toInt() shl 16)
        3 -> (0x00FFFFFF.toInt() and this) or (byte.toInt() shl 24)
        else -> this
    }
}

fun Long.splitSet(index: Int, byte: Byte): Long {
    return when (index) {
        0 -> (0xFF.inv().toLong() and this) or byte.toLong()
        1 -> (0xFF00.inv().toLong() and this) or (byte.toLong() shl 8)
        2 -> (0xFF0000.inv().toLong() and this) or (byte.toLong() shl 16)
        3 -> (0xFF000000.inv().toLong() and this) or (byte.toLong() shl 24)
        4 -> (0xFF00000000.inv().toLong() and this) or (byte.toLong() shl 32)
        5 -> (0xFF0000000000.inv().toLong() and this) or (byte.toLong() shl 40)
        6 -> (0xFF000000000000.inv().toLong() and this) or (byte.toLong() shl 48)
        7 -> (0x00FFFFFFFFFFFFFF.inv().toLong() and this) or (byte.toLong() shl 56)
        else -> this
    }
}