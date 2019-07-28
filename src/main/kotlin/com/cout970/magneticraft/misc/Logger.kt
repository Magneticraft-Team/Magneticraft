@file:Suppress("unused")

package com.cout970.magneticraft.misc

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import net.minecraft.client.resources.I18n
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.relauncher.Side

/**
 * Created by cout970 on 29/06/2016.
 */

fun logError(str: String, vararg args: Any) {
    Magneticraft.log.error("[$MOD_ID]$str", *args)
}

fun info(str: String, vararg args: Any) {
    Magneticraft.log.info(str, *args)
}

fun warn(str: String, vararg args: Any) {
    Magneticraft.log.warn(str, *args)
}

fun debug(vararg obj: Any?): Boolean {
    if (!Debug.DEBUG) return false
    obj.joinToString().forEach {
        print(it)
    }
    print('\n')
//    Magneticraft.log.info("[$MOD_ID][DEBUG]${obj.joinToString()}")
    return false
}

fun t(msg: String, vararg params: Any): String {
    if (FMLCommonHandler.instance().effectiveSide == Side.CLIENT) {
        return I18n.format(msg, *params)
    }
    return msg
}

fun String.toTextComponent(): ITextComponent = TextComponentString(this)


inline fun <R> logTime(prefix: String = "Time", func: () -> R): R {
    val start = System.currentTimeMillis()
    val res = func()
    val end = System.currentTimeMillis()
    info("$prefix ${end - start}ms")
    return res
}