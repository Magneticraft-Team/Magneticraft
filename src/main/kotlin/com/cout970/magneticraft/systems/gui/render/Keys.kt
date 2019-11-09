@file:Suppress("unused")

package com.cout970.magneticraft.systems.gui.render

import net.minecraft.client.Minecraft
import net.minecraft.client.util.InputMappings

/**
 * Created by cout970 on 08/07/2016.
 */

fun isMouseButtonDown(button: Int) = when (button) {
    0 -> Minecraft.getInstance().mouseHelper.isLeftDown
    1 -> Minecraft.getInstance().mouseHelper.isRightDown
    2 -> Minecraft.getInstance().mouseHelper.isMiddleDown
    else -> false
}

fun isCtrlKeyDown(): Boolean {
    return if (Minecraft.IS_RUNNING_ON_MAC) isKeyPressed(219) || isKeyPressed(220)
    else isKeyPressed(29) || isKeyPressed(157)
}

/**
 * Returns true if either shift key is down
 */
fun isShiftKeyDown(): Boolean = isKeyPressed(42) || isKeyPressed(54)

/**
 * Returns true if either alt key is down
 */
fun isAltKeyDown(): Boolean = isKeyPressed(56) || isKeyPressed(184)

fun isKeyComboCtrlX(keyID: Int): Boolean = keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlV(keyID: Int): Boolean = keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlC(keyID: Int): Boolean = keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlA(keyID: Int): Boolean = keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyPressed(key: Int) = InputMappings.isKeyDown(Minecraft.getInstance().mainWindow.handle, key)

fun keyboardEnableRepeatedEvents(value: Boolean) = Minecraft.getInstance().keyboardListener.enableRepeatEvents(value)