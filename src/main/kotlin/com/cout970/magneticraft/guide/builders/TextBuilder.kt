package com.cout970.magneticraft.guide.builders

import com.cout970.magneticraft.guide.components.Text
import com.cout970.magneticraft.util.box
import com.cout970.magneticraft.util.vector.Vec2d

class TextBuilder {
    var text = ""
    lateinit var position: Vec2d
    var size: Vec2d? = null

    fun build() = Text(position, size ?: text.box, text)
}