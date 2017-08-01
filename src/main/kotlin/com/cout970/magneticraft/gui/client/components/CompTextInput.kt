package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiTextField


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
        drawTextBox()
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