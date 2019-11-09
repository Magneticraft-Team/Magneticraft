package com.cout970.magneticraft.misc.player

import com.cout970.magneticraft.EntityPlayer
import net.minecraft.util.text.StringTextComponent
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.TranslationTextComponent

/**
 * Created by cout970 on 2017/02/20.
 */

fun EntityPlayer.sendMessage(str: String, vararg args: Any) {
    sendStatusMessage(TranslationTextComponent(str, *args), true)
}

fun EntityPlayer.sendUnlocalizedMessage(str: String) {
    sendStatusMessage(StringTextComponent(str), true)
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any, color: TextFormatting) {
    sendStatusMessage(TranslationTextComponent(str, *args).apply { style.color = color }, true)
}