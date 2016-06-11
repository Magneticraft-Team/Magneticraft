package com.cout970.magneticraft.config

import com.cout970.magneticraft.Magneticraft
import java.lang.reflect.Field
import java.util.*

/**
 * Created by cout970 on 16/05/2016.
 */
object ConfigHandler {

    val instance = Config
    var wrappers: MutableList<FieldWrapper> = mutableListOf()
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

    fun load(){
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
        val clazz = instance.javaClass
        val fields = clazz.declaredFields
        wrappers = LinkedList<FieldWrapper>()

        for (f in fields) {
            if (f.isAnnotationPresent(ConfigValue::class.java)) {
                f.isAccessible = true
                val type = f.type
                val annotation = f.getAnnotation(ConfigValue::class.java)
                var wrapper: FieldWrapper? = null
                if (type == Integer.TYPE) {
                    wrapper = IntegerFieldWrapper(f, annotation)
                } else if (type == java.lang.Double.TYPE) {
                    wrapper = DoubleFieldWrapper(f, annotation)
                } else if (type == java.lang.Boolean.TYPE) {
                    wrapper = BooleanFieldWrapper(f, annotation)
                } else if (type == String::class.java) {
                    wrapper = StringFieldWrapper(f, annotation)
                }else if(type == java.lang.Float.TYPE) {
                    wrapper = FloatFieldWrapper(f, annotation)
                }else if(type == OreConfig::class.java){
                    wrapper = OreConfigFieldWrapper(f, annotation)
                }
                if (wrapper != null) {
                    wrappers.add(wrapper)
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

        @Throws(IllegalAccessException::class)
        abstract fun read(handler: ConfigHandler)
    }

    class IntegerFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.INT) {

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val value = handler.config.getInteger(annotation.category, getKey(), field.getInt(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class DoubleFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.DOUBLE) {

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(), field.getDouble(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class FloatFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.DOUBLE) {

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(), field.getFloat(handler.instance).toDouble(), annotation.comment)
            field.set(handler.instance, value.toFloat())
        }
    }

    class BooleanFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.BOOLEAN) {

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val value = handler.config.getBoolean(annotation.category, getKey(), field.getBoolean(handler.instance), annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class StringFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.STRING) {

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val value = handler.config.getString(annotation.category, getKey(), field.get(handler.instance) as String, annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class OreConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation, ConfigValueType.ORE){

        @Throws(IllegalAccessException::class)
        override fun read(handler: ConfigHandler) {
            val category = annotation.category+"."+getKey().replace("Ore", "")
            //chunk
            val chunk = handler.config.getInteger(category, "chunkAmount", (field.get(handler.instance) as OreConfig).chunkAmount, "Amount of "+annotation.comment+" per chunk")
            //vein
            val vein = handler.config.getInteger(category, "veinAmount", (field.get(handler.instance) as OreConfig).veinAmount, "Amount of "+annotation.comment+" per vein")
            //max
            val max = handler.config.getInteger(category, "maxLevel", (field.get(handler.instance) as OreConfig).maxLevel, "Max level to generate the ore")
            //min
            val min = handler.config.getInteger(category, "minLevel", (field.get(handler.instance) as OreConfig).minLevel, "Min level to generate the ore")

            field.set(handler.instance, OreConfig(chunk, vein, max, min))
        }
    }
}