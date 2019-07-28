package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of
import com.google.gson.*
import java.io.File
import java.lang.reflect.Type

data class GuiConfig(
    val points: Map<String, IVector2>,
    val background: IVector2,
    val top: Boolean
) {

    // Needed for Gson to initialize default values
    constructor() : this(emptyMap(), vec2Of(176, 166), false)

    companion object {

        var config: Map<String, GuiConfig> = emptyMap()
            private set

        fun loadAll() {
            config = load()
        }
    }
}

private object Vec2Adapter : JsonDeserializer<IVector2> {
    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): IVector2 {
        return vec2Of(json.asJsonArray[0].asDouble, json.asJsonArray[1].asDouble)
    }
}

private fun load(): Map<String, GuiConfig> {
    val gson = GsonBuilder()
        .registerTypeAdapter(IVector2::class.java, Vec2Adapter)
        .create()

    val input = if (Debug.DEBUG && Debug.srcDir != null) {
        File(Debug.srcDir!!, "src/main/resources/assets/magneticraft/gui_config.json").inputStream()
    } else {
        val path = "assets/magneticraft/gui_config.json"
        Thread.currentThread().contextClassLoader.getResourceAsStream(path) ?: error("Not found: $path")
    }

    val obj = JsonParser().parse(input.bufferedReader()).asJsonObject

    val map = mutableMapOf<String, GuiConfig>()

    obj.entrySet().forEach { (name, json) ->
        map[name] = gson.fromJson(json!!, GuiConfig::class.java)
    }

    return map
}