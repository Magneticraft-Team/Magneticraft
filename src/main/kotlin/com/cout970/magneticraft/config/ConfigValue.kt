package com.cout970.magneticraft.config

/**
 * Created by cout970 on 16/05/2016.
 */

enum class ConfigValueType {
    INT, BOOLEAN, DOUBLE, STRING, ORE
}

@Retention(value = AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class ConfigValue(
    val category: String = CATEGORY_GENERAL,

    //if the key is empty then the field name will be used as a key
    val key: String = "",

    val comment: String
)