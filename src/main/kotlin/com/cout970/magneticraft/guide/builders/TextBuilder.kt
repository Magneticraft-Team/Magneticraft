package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.box
import com.cout970.magneticraft.guide.components.Text

class TextBuilder {
    var text = ""
    lateinit var position: Coords
    var size: Coords? = null

    fun build() = Text(position, size ?: text.box, text)
}