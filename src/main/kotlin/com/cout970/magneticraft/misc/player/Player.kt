package com.cout970.magneticraft.misc.player

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 2017/02/20.
 */

fun EntityPlayer.sendMessage(str: String, vararg args: Any) {
    addChatComponentMessage(TextComponentTranslation(str, *args))
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any, color: TextFormatting) {
    addChatComponentMessage(TextComponentTranslation(str, *args).apply { style.color = color })
}