package com.cout970.magneticraft

import net.minecraft.client.Minecraft
import net.minecraft.launchwrapper.Launch
import net.minecraft.util.Timer
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import java.io.File

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
}
