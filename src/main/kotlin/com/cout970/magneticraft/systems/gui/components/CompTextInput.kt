package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiTextField
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/08/01.
 */
class CompTextInput(
    fontRenderer: FontRenderer,
    override val pos: IVector2,
    override val size: IVector2
) : GuiTextField(0, fontRenderer, pos.xi, pos.yi,
    size.xi - fontRenderer.getCharWidth('_'), size.yi), IComponent {


    var updateFunc: ((CompTextInput) -> Unit)? = null
    override lateinit var gui: IGui

    init {
        enableBackgroundDrawing = false
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        x = gui.pos.xi + pos.xi
        y = gui.pos.yi + pos.yi
        if (isFocused) {
            val pos = gui.pos + pos - vec2Of(2)
            gui.drawColor(pos, pos + size + vec2Of(2, 0), 0x70654FF7)
        }
        drawTextBox()
        GL11.glColor4f(1f, 1f, 1f, 1f)
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            return mouseClicked(mouse.xi, mouse.yi, mouseButton)
        }
        if (mouseButton == 1 && mouse in (gui.pos + pos to size)) {
            text = ""
            updateFunc?.invoke(this)
            return true
        }
        return super.onMouseClick(mouse, mouseButton)
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        val size = text.length
        val ret = textboxKeyTyped(typedChar, keyCode)
        if (text.length != size) updateFunc?.invoke(this)
        return ret
    }
}