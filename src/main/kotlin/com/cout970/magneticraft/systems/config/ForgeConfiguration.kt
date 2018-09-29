package com.cout970.magneticraft.systems.config

import net.minecraftforge.common.config.Configuration
import java.io.File

/**
 * Created by cout970 on 16/05/2016.
 */
class ForgeConfiguration(file: File) : Configuration(file), IConfig {

    override fun getString(category: String, key: String, defaultValue: String, comment: String) =
        super.getString(key, category, defaultValue, comment)!!

    override fun getInteger(category: String, key: String, defaultValue: Int, comment: String) =
        get(category, key, defaultValue, comment).int

    override fun getBoolean(category: String, key: String, defaultValue: Boolean, comment: String) =
        super.getBoolean(key, category, defaultValue, comment)

    override fun getDouble(category: String, key: String, defaultValue: Double, comment: String) =
        get(category, key, defaultValue, comment).double

    override fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String): Array<String> =
        getStringList(key, category, defaultValue, comment)!!

    override fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String) =
        get(category, key, defaultValue, comment).intList!!

    override fun getBooleanArray(category: String, key: String, defaultValue: BooleanArray, comment: String) =
        get(category, key, defaultValue, comment).booleanList!!

    override fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String) =
        get(category, key, defaultValue, comment).doubleList!!

    override fun getString(category: String, key: String, defaultValue: String, comment: String, validValues: Array<String>) =
        super.getString(key, category, defaultValue, comment, validValues)!!

    override fun getInteger(category: String, key: String, defaultValue: Int, comment: String, min: Int, max: Int): Int {
        return getInt(key, category, defaultValue, min, max, comment)
    }

    override fun getDouble(category: String, key: String, defaultValue: Double, comment: String, min: Double, max: Double) =
        get(category, key, defaultValue, comment, min, max).double

    override fun getStringArray(category: String, key: String, defaultValue: Array<String>, comment: String, validValues: Array<String>): Array<String> =
        getStringList(key, category, defaultValue, comment, validValues, key)!!

    override fun getIntegerArray(category: String, key: String, defaultValue: IntArray, comment: String, min: Int, max: Int): IntArray {
        return get(category, key, defaultValue, comment, min, max).intList
    }

    override fun getDoubleArray(category: String, key: String, defaultValue: DoubleArray, comment: String, min: Double, max: Double) =
        get(category, key, defaultValue, comment, min, max).doubleList!!
}