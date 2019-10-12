package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.gui.GuiTextField
import net.minecraft.client.renderer.GlStateManager.color
import org.lwjgl.input.Keyboard

class SearchBar(
    override val pos: IVector2,
    val id: String = "no-id",
    var onChange: (String) -> Unit = {}
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 = vec2Of(90, 15)
    val background = vec2Of(125, 240)

    lateinit var textField: GuiTextField

    override fun init() {
        val charWidth = gui.fontHelper.getCharWidth('_')
        textField = GuiTextField(0, gui.fontHelper, pos.xi, pos.yi, size.xi - charWidth - 8, size.yi)
        textField.enableBackgroundDrawing = false
        textField.isFocused = Config.autoSelectSearchBar
        Keyboard.enableRepeatEvents(true)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(gui.pos + pos, size, background)

        textField.x = gui.pos.xi + pos.xi + 4
        textField.y = gui.pos.yi + pos.yi + 4

        if (textField.isFocused) {
            val pos = gui.pos + pos + vec2Of(1)
            gui.drawColor(pos, pos + size - vec2Of(2, 2), 0x70654FF7)
        }

        textField.drawTextBox()
        color(1f, 1f, 1f, 1f)
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            return textField.mouseClicked(mouse.xi, mouse.yi, mouseButton)
        }
        if (mouseButton == 1 && mouse in (gui.pos + pos to size)) {
            textField.text = ""
            onChange(textField.text)
            return true
        }
        return super.onMouseClick(mouse, mouseButton)
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        val size = textField.text.length
        val ret = textField.textboxKeyTyped(typedChar, keyCode)
        if (textField.text.length != size) onChange(textField.text)
        return ret
    }
}