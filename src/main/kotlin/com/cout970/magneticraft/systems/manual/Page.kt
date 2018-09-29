package com.cout970.magneticraft.systems.manual

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IGui


data class Page(val text: List<NormalTextBox>, val links: List<LinkTextBox> = emptyList(), val index: Int)

abstract class TextBox(val txt: String, val pos: IVector2, val page: Int) {

    abstract fun contains(mouse: IVector2, gui: IGui, offset: IVector2): Boolean
}

class NormalTextBox(txt: String, pos: IVector2, page: Int) : TextBox(txt, pos, page) {

    override fun contains(mouse: IVector2, gui: IGui, offset: IVector2): Boolean {
        val size = vec2Of(gui.fontHelper.getStringWidth(txt + " "), gui.fontHelper.FONT_HEIGHT)
        return mouse in (pos + offset to size)
    }
}

class LinkTextBox(
    val words: List<TextBox>,
    val linkSection: String,
    val linkPage: Int
) : TextBox(words.joinToString(), words[0].pos, words[0].page) {

    override fun contains(mouse: IVector2, gui: IGui, offset: IVector2): Boolean {
        return words.any { it.contains(mouse, gui, offset) }
    }
}