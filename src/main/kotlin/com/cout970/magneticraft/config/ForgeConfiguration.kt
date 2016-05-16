package com.cout970.magneticraft.config

import java.io.File

/**
 * Created by cout970 on 16/05/2016.
 */
class ForgeConfiguration(file: File) : net.minecraftforge.common.config.Configuration(file), IConfig {

    override fun getString(category: String, key: String, defaultValue: String, comment: String): String {
        return super.getString(key, category, defaultValue, comment)
    }

    override fun getInteger(category: String, key: String, defaultValue: Int, comment: String): Int {
        return get(category, key, defaultValue, comment).getInt()
    }

    override fun getBoolean(category: String, key: String, defaultValue: Boolean, comment: String): Boolean {
        return super.getBoolean(key, category, defaultValue, comment)
    }

    override fun getDouble(category: String, key: String, defaultValue: Double, comment: String): Double {
        return get(category, key, defaultValue, comment).getDouble()
    }

    override fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String): Array<String> {
        return getStringList(key, category, defaultValue, comment)
    }

    override fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String): IntArray {
        return get(category, key, defaultValue, comment).getIntList()
    }

    override fun getBooleanArray(category: String, key: String, defaultValue: BooleanArray, comment: String): BooleanArray {
        return get(category, key, defaultValue, comment).getBooleanList()
    }

    override fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String): DoubleArray {
        return get(category, key, defaultValue, comment).getDoubleList()
    }

    override fun getString(category: String, key: String, defaultValue: String, comment: String, validValues: Array<String>): String {
        return super.getString(key, category, defaultValue, comment, validValues)
    }

    override fun getInteger(category: String, key: String, defaultValue: Int, comment: String, min: Int, max: Int): Int {
        return getInt(key, category, defaultValue, min, max, comment)
    }

    override fun getDouble(category: String, key: String, defaultValue: Double, comment: String, min: Double, max: Double): Double {
        return get(category, key, defaultValue, comment, min, max).getDouble()
    }

    override fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String, validValues: Array<String>): Array<String> {
        return getStringList(key, category, defaultValue, comment, validValues, key)
    }

    override fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String, min: Int, max: Int): IntArray {
        return get(category, key, defaultValue, comment, min, max).getIntList()
    }

    override fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String, min: Double, max: Double): DoubleArray {
        return get(category, key, defaultValue, comment, min, max).getDoubleList()
    }
}