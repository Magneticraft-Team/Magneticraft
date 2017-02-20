package com.cout970.magneticraft.config

import java.io.File

interface IConfig {

    fun load()

    fun hasChanged(): Boolean

    fun save()

    @Suppress("unused")
    fun getConfigFile(): File

    fun getString(category: String, key: String, defaultValue: String, comment: String): String

    fun getInteger(category: String, key: String, defaultValue: Int, comment: String): Int

    fun getBoolean(category: String, key: String, defaultValue: Boolean, comment: String): Boolean

    fun getDouble(category: String, key: String, defaultValue: Double, comment: String): Double

    fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String): Array<String>

    fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String): IntArray

    fun getBooleanArray(category: String, key: String, defaultValue: BooleanArray, comment: String): BooleanArray

    fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String): DoubleArray

    fun getString(category: String, key: String, defaultValue: String, comment: String, validValues: Array<String>): String

    fun getInteger(category: String, key: String, defaultValue: Int, comment: String, min: Int, max: Int): Int

    fun getDouble(category: String, key: String, defaultValue: Double, comment: String, min: Double, max: Double): Double

    fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String, validValues: Array<String>): Array<String>

    fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String, min: Int, max: Int): IntArray

    fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String, min: Double, max: Double): DoubleArray
}