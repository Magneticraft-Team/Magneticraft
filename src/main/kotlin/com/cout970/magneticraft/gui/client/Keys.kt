@file:Suppress("unused")

package com.cout970.magneticraft.gui.client

import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse

/**
 * Created by cout970 on 08/07/2016.
 */

fun isMouseButtonDown(button: Int) = Mouse.isButtonDown(button)

fun isCtrlKeyDown(): Boolean {
    return if (Minecraft.IS_RUNNING_ON_MAC) Keyboard.isKeyDown(219) || Keyboard.isKeyDown(220)
    else Keyboard.isKeyDown(29) || Keyboard.isKeyDown(157)
}

/**
 * Returns true if either shift key is down
 */
fun isShiftKeyDown(): Boolean = Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54)

/**
 * Returns true if either alt key is down
 */
fun isAltKeyDown(): Boolean = Keyboard.isKeyDown(56) || Keyboard.isKeyDown(184)

fun isKeyComboCtrlX(keyID: Int): Boolean = keyID == 45 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlV(keyID: Int): Boolean = keyID == 47 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlC(keyID: Int): Boolean = keyID == 46 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyComboCtrlA(keyID: Int): Boolean = keyID == 30 && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()

fun isKeyPressed(key: Int) = Keyboard.isKeyDown(key)