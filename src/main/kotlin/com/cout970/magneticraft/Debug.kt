package com.cout970.magneticraft

import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import net.minecraft.client.Minecraft
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.Timer
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File
import java.io.FileWriter

@Suppress("unused")
/**
 * Created by cout970 on 11/06/2016.
 *
 * Stuff that only works in the dev-environment
 */
object Debug {
    val DEBUG = Launch.blackboard["fml.deobfuscatedEnvironment"] as Boolean
    var srcDir: File? = null

    fun preInit(event: FMLPreInitializationEvent) {
        srcDir = searchSourceDir(event.modConfigurationDirectory)
        if (srcDir == null) {
            com.cout970.magneticraft.util.error("Error trying to find the source directory")
        }
//        setTicksPerSecond(80)
//        Log.debug("Source folder found at ${srcDir?.absolutePath}")
//        for(i in ItemCrushedOre.CRUSHED_ORES.keys){
//            createItemJson("crushed_ore_${ItemCrushedOre.CRUSHED_ORES[i]}", "crushed_ore/${ItemCrushedOre.CRUSHED_ORES[i]}")
//        }
//        for(i in ItemIngots.INGOTS.keys){
//            createItemJson("ingots_${ItemIngots.INGOTS[i]}", "ingots/${ItemIngots.INGOTS[i]}")
//        }
    }

    fun searchSourceDir(configDir: File): File? {
        var temp: File? = configDir
        while (temp != null && temp.isDirectory) {
            if (File(temp, "build.gradle").exists()) {
                return temp
            }
            temp = temp.parentFile
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

    fun createItemJson(name: String, texName: String) {
        val path = srcDir!!.absolutePath + "/src/main/resources/assets/${MOD_ID}/models/item/$name.json"
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
        val path = srcDir!!.absolutePath + "/src/main/resources/assets/${MOD_ID}/models/block/$name.json"
        val file = File(path)
        if (file.exists()) return

        val gson = GsonBuilder().setPrettyPrinting().create()
        val json = JsonObject()
        val textures = JsonObject()

        json.addProperty("parent", "block/$parent")
        for ((i, j) in tex.entries) {
            textures.addProperty(i, "magneticraft:blocks/$j")
        }
        json.add("textures", textures)

        val string = gson.toJson(json)

        val writer = FileWriter(file)
        writer.write(string)
        writer.close()
    }
}
