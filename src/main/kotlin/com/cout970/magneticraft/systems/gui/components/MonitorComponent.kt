package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.computer.DeviceKeyboard
import com.cout970.magneticraft.systems.computer.DeviceMonitor
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_MONITOR_CLIPBOARD
import com.cout970.magneticraft.systems.gui.render.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager.color
import org.lwjgl.input.Keyboard

/**
 * Created by cout970 on 20/05/2016.
 */

class MonitorComponent(
        val tile: ITileRef,
        val monitor: DeviceMonitor,
        val keyboard: DeviceKeyboard
) : IComponent {

    companion object {
        @JvmStatic
        val TEXTURE = resource("textures/gui/monitor_text.png")
    }

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d(350, 230)
    override lateinit var gui: IGui

    override fun init() {
        Keyboard.enableRepeatEvents(true)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {

        gui.bindTexture(guiTexture("misc"))
        val start = gui.pos + vec2Of(11, 11)
        val end = start + vec2Of(328, 208)
        gui.drawColor(start - 1, end + 1, 0xFF1E1E1E.toInt())
        gui.drawColor(start, end, 0xFF232323.toInt())

        gui.bindTexture(guiTexture("monitor_text"))
        // 0 => amber 1, 1 => amber 2, 2 => white, 3 => green 1, 4 => apple 2, 5 => green 2, 6 => apple 2c, 7 => green 3, 8 => green 4
        when (Config.computerTextColor) {
            0 -> color(255f / 255f, 176f / 255f, 0f / 255f, 1.0f)// Amber
            1 -> color(255f / 255f, 204f / 255f, 0f / 255f, 1.0f) // Amb
            2 -> color(220f / 255f, 220f / 255f, 220f / 255f, 1.0f) // white
            3 -> color(51f / 255f, 255f / 255f, 0f / 255f, 1.0f) // Green 1
            4 -> color(51f / 255f, 255f / 255f, 51f / 255f, 1.0f) // Apple 2
            5 -> color(0f / 255f, 255f / 255f, 51f / 255f, 1.0f) // Green 2
            6 -> color(102f / 255f, 255f / 255f, 102f / 255f, 1.0f) // Apple 2c
            7 -> color(0f / 255f, 255f / 255f, 102f / 255f, 1.0f) // Green 3
            else -> color(47f / 255f, 140f / 255f, 64f / 255f, 1.0f) // Intellij
        }
        val lines = monitor.lines
        val columns = monitor.columns
        val scale = 4

        for (line in 0 until lines) {
            for (column in 0 until columns) {
                var character = monitor.getChar(line * columns + column) and 0xFF

                if (line == monitor.cursorLine && column == monitor.cursorColumn &&
                        tile.world.totalWorldTime % 10 >= 4) {

                    character = character xor 128
                }

                if (character != 32 && character != 0) {
                    val posX = gui.pos.xi + 15 + column * scale
                    val posY = gui.pos.yi + 15 + line * (scale + 2)
                    val x = character and 15
                    val y = character shr 4

                    Gui.drawScaledCustomSizeModalRect(
                            posX, posY,
                            x * 16f, y * 16f,
                            16, 16,
                            scale, scale,
                            256f, 256f
                    )
                }
            }
        }
        color(1.0f, 1.0f, 1.0f, 1.0f)
    }


    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        // Ignore ESC, otherwise you cannot exit the gui
        if (keyCode == 1) return false

        // Paste from clipboard
        // SHIFT + CTRL + ALT + V
        if (isShiftKeyDown() && isCtrlKeyDown() && isAltKeyDown() && keyCode == 47) {

            val str = GuiScreen.getClipboardString()

            if (str.isNotBlank()) {
                gui.container.sendUpdate(IBD().apply { setString(DATA_ID_MONITOR_CLIPBOARD, str) })
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
        var intChar = typedChar.toInt()

        if (isCtrlKeyDown() && keyCode != 29) {
            if (intChar in 65..90) {
                intChar += 96
            }
        }

        val key = when (intChar) {
            // Unknown keys
            202, 204, 206 -> intChar

            // printable ascii chars
            in 32..126 -> intChar

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
        Keyboard.enableRepeatEvents(false)
    }
}