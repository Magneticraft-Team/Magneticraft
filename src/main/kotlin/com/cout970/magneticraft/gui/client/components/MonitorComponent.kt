package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.computer.DeviceKeyboard
import com.cout970.magneticraft.computer.DeviceMonitor
import com.cout970.magneticraft.gui.client.core.*
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.gui.common.core.DATA_ID_MONITOR_CLIPBOARD
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.gui.GuiScreen
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 20/05/2016.
 */

class MonitorComponent(
    val tile: ITileRef,
    val monitor: DeviceMonitor,
    val keyboard: DeviceKeyboard,
    val container: ContainerBase,
    val green: Boolean
) : IComponent {

    companion object {
        @JvmStatic
        val TEXTURE = resource("textures/gui/monitor_text.png")
    }

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d(350, 230)
    override lateinit var gui: IGui

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {

        gui.bindTexture(TEXTURE)
        if (green) {
            GL11.glColor4f(76.0f / 256f, 1.0f, 0.0f, 1.0f)
        }
        val lines = monitor.lines
        val columns = monitor.columns
        val scale = 4
        val charSize = Vec2d(scale, scale)

        for (line in 0 until lines) {
            for (column in 0 until columns) {
                var character = monitor.getChar(line * columns + column) and 0xFF
                if (line == monitor.cursorLine && column == monitor.cursorColumn && tile.world.worldTime % 20 >= 10) {
                    character = character xor 128
                }
                if (character != 32 && character != 0) {
                    val pos = gui.pos + Vec2d(15 + column * scale, 15 + line * (scale + 2))
                    val x = character and 15
                    val y = character shr 4

                    gui.drawTexture(DrawableBox(pos, charSize, Vec2d(x, y) * 16, Vec2d(16, 16)))
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }


    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        // Ignore ESC, otherwise you cannot exit the gui
        if (keyCode == 1) return false

        // Paste from clipboard
        // SHIFT + CTRL + ALT + V
        if (isShiftKeyDown() && isCtrlKeyDown() && isAltKeyDown() && keyCode == 47) {

            val str = GuiScreen.getClipboardString()

            if (str.isNotBlank()) {
                container.sendUpdate(IBD().apply { setString(DATA_ID_MONITOR_CLIPBOARD, str) })
            }
        }

        sendKey(typedChar, keyCode, true)
        return true
    }

    override fun onKeyReleased(typedChar: Char, keyCode: Int): Boolean {
        sendKey(typedChar, keyCode, false)
        return true
    }

    fun sendKey(typedChar: Char, keyCode: Int, press: Boolean) {

        val key = when (typedChar.toInt()) {
            // Unknown keys
            202, 204, 206 -> typedChar.toInt()

            // printable ascii chars
            in 32..126 -> typedChar.toInt()

            else -> when (keyCode) {
                0 -> return // Unknown
                1 -> return // ESC
                58 -> return  // Caps lock

                200 -> 1 // UP
                208 -> 2 // DOWN
                203 -> 3 // LEFT
                205 -> 4 // RIGHT

                199 -> 5 // HOME
                201 -> 6 // PRIOR (re-pag)
                207 -> 7 // END
                209 -> 8 // NEXT (av-pag)
                210 -> 10 // INSERT
                211 -> 13 // DELETE

                14 -> 11 // BACK
                15 -> 9 // TAB
                28 -> 10 // RETURN
                29 -> 14 // LCONTROL

                42 -> 15 // LSHIFT
                54 -> 16 // RSHIFT
                56 -> 17 // LMENU

                59 -> 18 // F1
                60 -> 19 // F2
                61 -> 20 // F3
                62 -> 21 // F4
                63 -> 22 // F5
                64 -> 23 // F6
                65 -> 24 // F7
                66 -> 25 // F8
                67 -> 26 // F9
                68 -> 27 // F10

                157 -> 28 // RCONTROL
                184 -> 29 // RMENU

                219 -> 30 // LMETA
                220 -> 31 // RMETA

                else -> {
                    return
                }
            }
        }
//        debug("sendKey: $key (${key.toChar()})")

        sendKey(key, keyCode, press)
    }

    fun sendKey(key: Int, code: Int, press: Boolean) {
        if (press) {
            keyboard.onKeyPress(key, code)
        } else {
            keyboard.onKeyRelease(key, code)
        }
        gui.container.detectAndSendChanges()
    }

    override fun onGuiClosed() {
        keyboard.reset()
    }
}