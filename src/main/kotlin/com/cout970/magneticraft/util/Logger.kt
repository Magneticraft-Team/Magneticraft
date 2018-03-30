@file:Suppress("unused")

package com.cout970.magneticraft.util

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.Magneticraft
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString

/**
 * Created by cout970 on 29/06/2016.
 */

fun logError(str: String, vararg args: Any) {
    Magneticraft.log.error("[$MOD_ID]$str", *args)
}

fun info(str: String, vararg args: Any) {
    Magneticraft.log.info(str, *args)
}

fun debug(vararg obj: Any?) {
    if (!Debug.DEBUG) return
    obj.joinToString().forEach {
        print(it)
    }
    print('\n')
//    Magneticraft.log.info("[$MOD_ID][DEBUG]${obj.joinToString()}")
}

fun String.toTextComponent(): ITextComponent = TextComponentString(this)