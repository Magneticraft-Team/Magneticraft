package com.cout970.magneticraft

import com.cout970.magneticraft.util.Log
import com.cout970.magneticraft.util.MODID
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.block.Block
import net.minecraft.client.Minecraft
import net.minecraft.item.Item
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.Timer
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File
import java.io.FileWriter

/**
 * Created by cout970 on 11/06/2016.
 */
object Debug {

    val DEBUG = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
    var srcDir: File? = null

    fun preInit(event: FMLPreInitializationEvent) {
        srcDir = searchSourceDir(event.modConfigurationDirectory)
        if (srcDir == null) {
            Log.debug("Error trying to find the source directory")
            return
        }
        Log.debug("Source folder found at ${srcDir?.absolutePath}")
        createBlockModelJson("ore_block_copper", "cube_all", mapOf("all" to "ore_block/copper"))
        createBlockModelJson("ore_block_lead", "cube_all", mapOf("all" to "ore_block/lead"))
        createBlockModelJson("ore_block_cobalt", "cube_all", mapOf("all" to "ore_block/cobalt"))
        createBlockModelJson("ore_block_tungsten", "cube_all", mapOf("all" to "ore_block/tungsten"))
    }

    fun searchSourceDir(configDir: File): File {
        var temp = configDir
        while (temp != null && temp.isDirectory) {
            if (File(temp, "build.gradle").exists()) {
                return temp;
            }
            temp = temp.parentFile;
        }
        return temp
    }

    //useful function to change the amount of tick per second used in minecraft
    fun setTicksPerSecond(tps: Int) {
        val timerField = Minecraft::class.java.getDeclaredField("timer")
        timerField.isAccessible = true
        val timer = timerField.get(Minecraft.getMinecraft()) as Timer
        val tickField = Timer::class.java.getDeclaredField("ticksPerSecond")
        tickField.isAccessible = true
        tickField.set(timer, tps.toFloat())
    }

    fun createItemJson(item: Item, texName: String) {
        val name = item.registryName.resourcePath
        val path = srcDir!!.absolutePath + "/src/main/resources/assets/$MODID/models/item/$name.json"
        val file = File(path)
        if (file.exists()) return

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = JsonObject()
        val textures = JsonObject()

        json.addProperty("parent", "item/generated")
        textures.addProperty("layer0", "magneticraft:items/$texName")
        json.add("textures", textures)

        val string = gson.toJson(json)

        val writer = FileWriter(file)
        writer.write(string)
        writer.close()
    }

    fun createBlockModelJson(name: String, parent: String, tex: Map<String, String>) {
        val path = srcDir!!.absolutePath + "/src/main/resources/assets/$MODID/models/block/$name.json"
        val file = File(path)
        if (file.exists()) return

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = JsonObject()
        val textures = JsonObject()

        json.addProperty("parent", "block/$parent")
        for ((i, j) in tex) {
            textures.addProperty(i, "magneticraft:blocks/$j")
        }
        json.add("textures", textures)

        val string = gson.toJson(json)

        val writer = FileWriter(file)
        writer.write(string)
        writer.close()
    }

    fun createBlockstateJson(block: Block, model:String){

        val name = block.registryName.resourcePath
        val path = srcDir!!.absolutePath + "/src/main/resources/assets/$MODID/blockstates/$name.json"
        val file = File(path)
        if (file.exists()) return

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = JsonObject()
        val defaults = JsonObject()
        val variants = JsonObject()
        val normalArray = JsonArray()
        val inventoryArray = JsonArray()
        val inventoryObject = JsonObject()

        defaults.addProperty("model", "$MODID:$model")

        normalArray.add(JsonObject())
        variants.add("normal", normalArray)

        inventoryObject.addProperty("transform", "forge:default-block")
        inventoryArray.add(inventoryObject)
        variants.add("inventory", JsonArray())

        json.addProperty("forge_marker", 1)
        json.add("defaults", defaults)
        json.add("variants", variants)

        val string = gson.toJson(json)

        val writer = FileWriter(file)
        writer.write(string)
        writer.close()
    }
}