package com.cout970.magneticraft.guide

import coffee.cypher.mcextlib.extensions.resources.getResource
import com.cout970.magneticraft.guide.components.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import com.google.gson.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.JsonToNBT
import net.minecraft.util.ResourceLocation
import java.io.File
import java.lang.reflect.Type

/**
 * Created by cout970 on 2016/10/07.
 */
object JsonSerializer {

    val GSON = GsonBuilder().setPrettyPrinting()
            .addSerializationExclusionStrategy(Estrategy)
            .registerTypeAdapter(Vec2d::class.java, Vector2Serializer)
            .registerTypeAdapter(ResourceLocation::class.java, ResourceLocationSerializer)
            .registerTypeAdapter(PageComponent::class.java, PageComponentSerializer)
            .registerTypeAdapter(ItemStack::class.java, ItemStackSerializer).create()!!

    val BOOK_LOCATION = resource("guide/book.json")

    fun test() {
//        write(File("test.json"), Book(listOf(
//                BookEntry("Title1", listOf(
//                        BookPage(listOf(
//                                Image(Vec2d(0, 0), Vec2d(16, 16), resource("imageLocation")),
//                                Link(LinkInfo("entryLinkName", 0), Text(Vec2d(0, 0), Vec2d(16, 16), "Link")),
//                                StackIcon(Vec2d(0, 0), ItemStack(Items.APPLE)),
//                                Text(Vec2d(0, 0), Vec2d(64, 64), "sampleText")
//                        ))
//                ))
//        )))
        write(File("test.json"), createBook())
    }

    fun read(): Book {
        val reader = BOOK_LOCATION.getResource().inputStream.reader()
        return GSON.fromJson(reader, Book::class.java)
    }

    fun write(file: File, book: Book) {
        file.outputStream().writer().use {
            GSON.toJson(book, it)
        }
    }
}


@kotlin.annotation.Retention(AnnotationRetention.RUNTIME)
@kotlin.annotation.Target(AnnotationTarget.FIELD)
annotation class JsonIgnore()

object Estrategy : ExclusionStrategy {

    override fun shouldSkipClass(clazz: Class<*>?): Boolean = false

    override fun shouldSkipField(f: FieldAttributes): Boolean {
        return f.getAnnotation(JsonIgnore::class.java) != null
    }
}

object PageComponentSerializer : JsonDeserializer<PageComponent>, com.google.gson.JsonSerializer<PageComponent> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): PageComponent {
        return when (json.asJsonObject["id"].asString) {
            "image" -> JsonSerializer.GSON.fromJson(json, Image::class.java)
            "link" -> JsonSerializer.GSON.fromJson(json, Link::class.java)
            "recipe" -> JsonSerializer.GSON.fromJson(json, Recipe::class.java)
            "stack" -> JsonSerializer.GSON.fromJson(json, StackIcon::class.java)
            "text" -> JsonSerializer.GSON.fromJson(json, Text::class.java)
            else -> throw IllegalStateException("Invalid gui component id: ${json.asJsonObject["id"]} in json object: $json")
        }
    }

    override fun serialize(src: PageComponent, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonSerializer.GSON.toJsonTree(src)
    }
}

object Vector2Serializer : JsonDeserializer<Vec2d>, com.google.gson.JsonSerializer<Vec2d> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): Vec2d {
        val array = json.asJsonArray
        return Vec2d(array[0].asDouble, array[1].asDouble)
    }

    override fun serialize(src: Vec2d, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonArray().apply { add(JsonPrimitive(src.x)); add(JsonPrimitive(src.y)) }
    }
}

object ItemStackSerializer : JsonDeserializer<ItemStack>, com.google.gson.JsonSerializer<ItemStack> {

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ItemStack {
        val obj = json.asJsonObject
        val item = Item.getByNameOrId(obj["item"].asString)
        val amount = obj["amount"]?.asInt ?: 0
        val damage = obj["damage"]?.asInt ?: 0

        val stack = ItemStack(item, amount, damage)

        if(obj["nbt"] != null) {
            stack.tagCompound = JsonToNBT.getTagFromJson(obj["nbt"].asString)
        }
        return stack
    }

    override fun serialize(src: ItemStack, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        val name = src.item.registryName
        val amount = src.stackSize
        val damage = src.itemDamage
        val nbt = src.tagCompound

        val obj = JsonObject()
        obj.addProperty("item", name.toString())
        obj.addProperty("amount", amount)
        obj.addProperty("damage", damage)
        if (nbt != null) {
            obj.addProperty("nbt", nbt.toString())
        }
        return obj
    }
}

object ResourceLocationSerializer : com.google.gson.JsonSerializer<ResourceLocation>, JsonDeserializer<ResourceLocation> {

    override fun serialize(src: ResourceLocation, typeOfSrc: Type, context: JsonSerializationContext): JsonElement {
        return JsonPrimitive(src.toString())
    }

    override fun deserialize(json: JsonElement, typeOfT: Type, context: JsonDeserializationContext): ResourceLocation {
        return ResourceLocation(json.asString)
    }
}