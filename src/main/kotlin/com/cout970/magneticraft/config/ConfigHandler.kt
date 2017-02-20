package com.cout970.magneticraft.config

import com.cout970.magneticraft.Magneticraft
import java.lang.reflect.Field

/**
 * Created by cout970 on 16/05/2016.
 */
object ConfigHandler {

    val instance = Config
    val wrappers = mutableListOf<FieldWrapper>()
    val config: IConfig

    init {
        config = ForgeConfiguration(Magneticraft.configFile)
        loadFields()
    }

    fun save() {
        if (config.hasChanged()) {
            config.save()
        }
    }

    fun load() {
        config.load()
    }

    fun read() {
        for (fw in wrappers) {
            try {
                fw.read(this)
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
        }
    }

    fun loadFields() {
        val clazz = instance::class.java
        val fields = clazz.declaredFields
        wrappers.clear()

        for (f in fields) {
            if (f.isAnnotationPresent(ConfigValue::class.java)) {
                f.isAccessible = true
                val annotation = f.getAnnotation(ConfigValue::class.java)

                when (f.type) {
                    Int::class.java -> IntegerFieldWrapper(f, annotation)
                    Double::class.java -> DoubleFieldWrapper(f, annotation)
                    Boolean::class.java -> BooleanFieldWrapper(f, annotation)
                    String::class.java -> StringFieldWrapper(f, annotation)
                    Float::class.java -> FloatFieldWrapper(f, annotation)
                    OreConfig::class.java -> OreConfigFieldWrapper(f, annotation)
                    GaussOreConfig::class.java -> GaussOreConfigFieldWrapper(f, annotation)
                    else -> null
                }?.let {
                    wrappers += it
                }
            }
        }
    }

    abstract class FieldWrapper(val field: Field, val annotation: ConfigValue, val type: ConfigValueType) {

        fun getKey(): String {
            if (annotation.key == "") {
                return field.name
            }
            return annotation.key
        }

        abstract fun read(handler: ConfigHandler)
    }

    class IntegerFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.INT) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getInteger(annotation.category, getKey(), field.getInt(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class DoubleFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.DOUBLE) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(), field.getDouble(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class FloatFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.DOUBLE) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(), field.getFloat(handler.instance).toDouble(), annotation.comment)
            field.set(handler.instance, value.toFloat())
        }
    }

    class BooleanFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.BOOLEAN) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getBoolean(annotation.category, getKey(), field.getBoolean(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class StringFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.STRING) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getString(annotation.category, getKey(), field.get(handler.instance) as String, annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class OreConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.ORE) {

        override fun read(handler: ConfigHandler) {
            val category = annotation.category + "." + getKey().replace("Ore", "")

            val active = handler.config.getBoolean(category, "active", (field.get(handler.instance) as OreConfig).active, "If ${annotation.comment} should be generated or not")

            val chunk = handler.config.getInteger(category, "chunkAmount", (field.get(handler.instance) as OreConfig).chunkAmount, "Amount of ${annotation.comment} per chunk")
            val vein = handler.config.getInteger(category, "veinAmount", (field.get(handler.instance) as OreConfig).veinAmount, "Amount of ${annotation.comment} per vein")
            val max = handler.config.getInteger(category, "maxLevel", (field.get(handler.instance) as OreConfig).maxLevel, "Max level to generate the ore")
            val min = handler.config.getInteger(category, "minLevel", (field.get(handler.instance) as OreConfig).minLevel, "Min level to generate the ore")

            field.set(handler.instance, OreConfig(chunk, vein, max, min, active))
        }
    }

    class GaussOreConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.ORE) {
        override fun read(handler: ConfigHandler) {
            val category = annotation.category + "." + getKey().replace("Ore", "")

            val active = handler.config.getBoolean(category, "active", (field.get(handler.instance) as GaussOreConfig).active, "If ${annotation.comment} should be generated or not")

            val chunk = handler.config.getInteger(category, "chunkAmount", (field.get(handler.instance) as GaussOreConfig).chunkAmount, "Amount of ${annotation.comment} per chunk")
            val vein = handler.config.getInteger(category, "veinAmount", (field.get(handler.instance) as GaussOreConfig).veinAmount, "Amount of ${annotation.comment} per vein")

            val maxY = handler.config.getInteger(category, "maxLevel", (field.get(handler.instance) as GaussOreConfig).maxLevel, "Max level to generate the ore")
            val minY = handler.config.getInteger(category, "minLevel", (field.get(handler.instance) as GaussOreConfig).minLevel, "Min level to generate the ore")

            val minVeins = handler.config.getInteger(category, "minAmount", (field.get(handler.instance) as GaussOreConfig).minAmountPerChunk, "Min amount of veins of ore per chunk")
            val maxVeins = handler.config.getInteger(category, "maxAmount", (field.get(handler.instance) as GaussOreConfig).maxAmountPerChunk, "Max amount of veins of ore per chunk")

            val deviation = handler.config.getDouble(category, "maxAmount", (field.get(handler.instance) as GaussOreConfig).deviation.toDouble(), "Standard deviation of the amount of veins per chunk").toFloat()

            field.set(handler.instance, GaussOreConfig(minVeins, maxVeins, deviation, chunk, vein, maxY, minY, active))
        }
    }
}