package com.cout970.magneticraft.client.render

import com.cout970.magneticraft.client.render.model.BlockModel
import com.cout970.magneticraft.util.MODID
import com.google.gson.*
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.resources.IResourceManager
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.ICustomModelLoader
import net.minecraftforge.client.model.IModel
import java.io.InputStreamReader
import java.lang.reflect.Type
import java.util.*

/**
 * Created by cout970 on 13/05/2016.
 */

object ModelLoader : ICustomModelLoader {

    lateinit var resourceManager: IResourceManager
    lateinit var modelMap: ModelMap

    override fun onResourceManagerReload(resourceManager: IResourceManager?) {
        this.resourceManager = resourceManager!!
        modelMap = loadModelMap()
    }

    override fun accepts(modelLocation: ResourceLocation?): Boolean {
        return getEntry(modelLocation) != null
    }

    override fun loadModel(modelLocation: ResourceLocation?): IModel? {
        val entry = getEntry(modelLocation)
        return entry?.model?.getIModel()
    }

    fun ResourceLocation.toModelResourceLocation() = if(this is ModelResourceLocation) this else ModelResourceLocation(this, "none")

    fun getEntry(modelLocation: ResourceLocation?): Entry? {
        if (modelLocation == null) return null
        return modelMap.map[modelLocation.toModelResourceLocation()];
    }

    fun loadModelMap(): ModelMap {
        val res = resourceManager.getResource(ResourceLocation(MODID, "models/models.json"))
        val gson = GsonBuilder().registerTypeAdapter(ModelMap::class.java, Deserializer).create()
        val adapter = gson.getAdapter(ModelMap::class.java)
        return adapter.fromJson(InputStreamReader(res.inputStream))
    }

    object Deserializer : JsonDeserializer<ModelMap> {

        fun JsonObject.getRes(): ResourceLocation {
            return ResourceLocation(this.get("id").asString);
        }

        fun JsonArray.toList(): List<ResourceLocation> {
            val list = ArrayList<ResourceLocation>()
            for (i in this) {
                list.add(ResourceLocation(i.asString))
            }
            return list
        }

        override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): ModelMap? {
            val root = json?.asJsonObject!!
            val blockList = root.getAsJsonArray("blocks")
            //list of entrys with model locations, texture locations and any other data
            val list = LinkedHashMap<ModelResourceLocation, Entry>()

            for (i in blockList) {
                val entry = i.asJsonObject
                val id = entry.getRes()
                val variants = entry.get("variants").asJsonArray
                for (s in variants) {
                    val state = s.asJsonObject
                    val variant = state.get("variant").asString
                    val type = state.get("type").asString

                    if (type == "block") {
                        val texArray = state.get("textures").asJsonArray
                        list.put(ModelResourceLocation(id, variant), Entry(ModelResourceLocation(id, variant), BlockModel(texArray.toList())))
                    }
                }
            }

            val itemList = root.getAsJsonArray("items")

            for(i in itemList){
                val entry = i.asJsonObject
                val id = entry.getRes()
                val variants = entry.get("variants").asJsonArray
                for(s in variants){
                    val meta = s.asJsonObject
                    val variant = meta.get("variant").asString
                    val type = meta.get("type").asString

                    if (type == "block") {
                        val texArray = meta.get("textures").asJsonArray
                        list.put(ModelResourceLocation(id, variant), Entry(ModelResourceLocation(id, variant), BlockModel(texArray.toList())))
                    }
                }
            }

            return ModelMap(list)
        }
    }

    data class ModelMap(val map: Map<ModelResourceLocation, Entry>)

    data class Entry(val id: ModelResourceLocation, val model: Model)

    interface Model {
        fun getIModel(): IModel?
    }

    data class ItemModel(
            val layers: List<ResourceLocation>
    ) : Model {
        override fun getIModel(): IModel? {
            throw UnsupportedOperationException()
        }
    }

    data class TcnModel(
            val modelLoc: ResourceLocation,
            val textureLoc: ResourceLocation
    ) : Model {
        override fun getIModel(): IModel? {
            throw UnsupportedOperationException()
        }
    }

    data class ObjModel(
            val modelLoc: ResourceLocation,
            val textureLoc: ResourceLocation
    ) : Model {
        override fun getIModel(): IModel? {
            throw UnsupportedOperationException()
        }
    }
}


