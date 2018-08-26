package com.cout970.magneticraft.misc.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 2017/02/20.
 */

fun EntityPlayer.sendMessage(str: String, vararg args: Any) {
    sendStatusMessage(TextComponentTranslation(str, *args), true)
}

fun EntityPlayer.sendUnlocalizedMessage(str: String) {
    sendStatusMessage(TextComponentString(str), true)
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any, color: TextFormatting) {
    sendStatusMessage(TextComponentTranslation(str, *args).apply { style.color = color }, true)
}