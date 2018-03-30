package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.computer.DeviceMonitor
import com.cout970.magneticraft.gui.client.core.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 20/05/2016.
 */

class MonitorComponent(val monitor: DeviceMonitor, val green: Boolean) : IComponent {

    val TEXTURE = resource("textures/gui/monitor_text.png")
    var pressedKeyNum = -1
    var pressedKeyCode: Int = 0

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
                if (line == monitor.cursorLine && column == monitor.cursorColumn && monitor.world.worldTime % 20 >= 10) {
                    character = character xor 128
                }
                if (character != 32 && character != 0) {
                    val pos = gui.pos + Vec2d(15 + column * scale, 15 + line * (scale + 2))
                    val x = character and 15
                    val y = character shr 4
                    gui.drawTexture(DrawableBox(pos, charSize, Vec2d(x, y) * 16))
                }
            }
        }
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f)
    }


    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        if (typedChar.toInt() == 27) return false
        var shift = 0
        if (isShiftKeyDown()) shift = shift or 64
        if (isCtrlKeyDown()) shift = shift or 32
        when (typedChar.toByte().toInt()) {
            199 -> sendKey(132 or shift, keyCode)
            200 -> sendKey(128 or shift, keyCode)
            201, 202, 204, 206,
            209 -> sendKey(typedChar.toInt(), keyCode)
            203 -> sendKey(130 or shift, keyCode)
            205 -> sendKey(131 or shift, keyCode)
            207 -> sendKey(133 or shift, keyCode)
            208 -> sendKey(129 or shift, keyCode)
            210 -> sendKey(134 or shift, keyCode)
            0 -> if (keyCode != 54 && keyCode != 42 && keyCode != 56 && keyCode != 184 && keyCode != 29 &&
                    keyCode != 221 && keyCode != 157 && (keyCode < 59 || keyCode > 70) && keyCode != 87 &&
                    keyCode != 88 && keyCode != 197 && keyCode != 183 && keyCode != 0) {
                sendKey(keyCode, keyCode)
            }
            else -> if (typedChar.toInt() in 1..127) {
                sendKey(typedChar.toInt(), keyCode)
            }
        }
        return true
    }

    fun sendKey(key: Int, num: Int) {
        pressedKeyNum = num
        pressedKeyCode = key
        monitor.onKeyPressed(key)
        gui.container.detectAndSendChanges()
    }
}