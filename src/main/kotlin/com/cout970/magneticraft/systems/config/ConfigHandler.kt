package com.cout970.magneticraft.systems.config

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.common.config.ConfigElement
import net.minecraftforge.fml.client.config.IConfigElement
import net.minecraftforge.fml.client.event.ConfigChangedEvent
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
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

    fun init(){
        MinecraftForge.EVENT_BUS.register(this)
        load()
        read()
        save()
    }

    @SubscribeEvent
    fun onConfigReload(event: ConfigChangedEvent.OnConfigChangedEvent){
        if(event.modID == MOD_ID){
            read()
            save()
        }
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
                    OilGenConfig::class.java -> OilGenConfigFieldWrapper(f, annotation)
                    else -> null
                }?.let {
                    wrappers += it
                }
            }
        }
    }

    fun getConfigElements(): List<IConfigElement> {
        val cnf = config as ForgeConfiguration
        return listOf(
                ConfigElement(cnf.getCategory(CATEGORY_ORES)),
                ConfigElement(cnf.getCategory(CATEGORY_ENERGY)),
                ConfigElement(cnf.getCategory(CATEGORY_GUI)),
                ConfigElement(cnf.getCategory(CATEGORY_PC)),
                ConfigElement(cnf.getCategory(CATEGORY_MACHINES))
        )
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

    class IntegerFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.INT) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getInteger(annotation.category, getKey(), field.getInt(handler.instance),
                    annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class DoubleFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.DOUBLE) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(), field.getDouble(handler.instance),
                    annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class FloatFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.DOUBLE) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getDouble(annotation.category, getKey(),
                    field.getFloat(handler.instance).toDouble(), annotation.comment)
            field.set(handler.instance, value.toFloat())
        }
    }

    class BooleanFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.BOOLEAN) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getBoolean(annotation.category, getKey(), field.getBoolean(handler.instance),
                    annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class StringFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.STRING) {

        override fun read(handler: ConfigHandler) {
            val value = handler.config.getString(annotation.category, getKey(), field.get(handler.instance) as String,
                    annotation.comment)
            field.set(handler.instance, value)
        }
    }

    class OreConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.ORE) {

        override fun read(handler: ConfigHandler) {
            val category = annotation.category + "." + getKey().replace("Ore", "")

            val active = handler.config.getBoolean(category, "active",
                    (field.get(handler.instance) as OreConfig).active,
                    "If ${annotation.comment} should be generated or not")

            val chunk = handler.config.getInteger(category, "chunkAmount",
                    (field.get(handler.instance) as OreConfig).chunkAmount, "Amount of ${annotation.comment} per chunk")
            val vein = handler.config.getInteger(category, "veinAmount",
                    (field.get(handler.instance) as OreConfig).veinAmount, "Amount of ${annotation.comment} per vein")
            val max = handler.config.getInteger(category, "maxLevel",
                    (field.get(handler.instance) as OreConfig).maxLevel, "Max level to generate the ore")
            val min = handler.config.getInteger(category, "minLevel",
                    (field.get(handler.instance) as OreConfig).minLevel, "Min level to generate the ore")

            field.set(handler.instance, OreConfig(chunk, vein, max, min, active))
        }
    }

    class GaussOreConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.ORE) {
        override fun read(handler: ConfigHandler) {
            val category = annotation.category + "." + getKey().replace("Ore", "")

            val active = handler.config.getBoolean(category, "active",
                    (field.get(handler.instance) as GaussOreConfig).active,
                    "If ${annotation.comment} should be generated or not")

            val chunk = handler.config.getInteger(category, "chunkAmount",
                    (field.get(handler.instance) as GaussOreConfig).chunkAmount,
                    "Amount of ${annotation.comment} per chunk")
            val vein = handler.config.getInteger(category, "veinAmount",
                    (field.get(handler.instance) as GaussOreConfig).veinAmount,
                    "Amount of ${annotation.comment} per vein")

            val maxY = handler.config.getInteger(category, "maxLevel",
                    (field.get(handler.instance) as GaussOreConfig).maxLevel, "Max level to generate the ore")
            val minY = handler.config.getInteger(category, "minLevel",
                    (field.get(handler.instance) as GaussOreConfig).minLevel, "Min level to generate the ore")

            val minVeins = handler.config.getInteger(category, "minAmount",
                    (field.get(handler.instance) as GaussOreConfig).minAmountPerChunk,
                    "Min amount of veins of ore per chunk")
            val maxVeins = handler.config.getInteger(category, "maxAmount",
                    (field.get(handler.instance) as GaussOreConfig).maxAmountPerChunk,
                    "Max amount of veins of ore per chunk")

            val deviation = handler.config.getDouble(category, "deviation",
                    (field.get(handler.instance) as GaussOreConfig).deviation.toDouble(),
                    "Standard deviation of the amount of veins per chunk").toFloat()

            field.set(handler.instance, GaussOreConfig(minVeins, maxVeins, deviation, chunk, vein, maxY, minY, active))
        }
    }

    class OilGenConfigFieldWrapper(field: Field, annotation: ConfigValue) : FieldWrapper(field, annotation,
            ConfigValueType.ORE) {
        override fun read(handler: ConfigHandler) {
            val category = annotation.category + "." + getKey().replace("Ore", "")

            val active = handler.config.getBoolean(category, "active",
                    (field.get(handler.instance) as OilGenConfig).active,
                    "If ${annotation.comment} should be generated or not")

            val probability = handler.config.getDouble(category, "probability",
                    (field.get(handler.instance) as OilGenConfig).prob.toDouble(),
                    "Probability of each block of oil to be generated")
            val distance = handler.config.getInteger(category, "distance",
                    (field.get(handler.instance) as OilGenConfig).distance,
                    "Distance between oil deposits in multiples of 8")


            field.set(handler.instance, OilGenConfig(probability.toFloat(), distance, active))
        }
    }
}