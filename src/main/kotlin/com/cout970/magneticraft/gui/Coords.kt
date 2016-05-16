package com.cout970.magneticraft.gui

import net.minecraft.client.Minecraft

data class Coords(val x: Int, val y: Int) {
    operator fun plus(that: Coords) = Coords(x + that.x, y + that.y)

    operator fun minus(that: Coords) = Coords(x - that.x, y - that.y)

    fun inside(start: Coords, end: Coords) = (this.x in (start.x..end.x)) && (this.y in (start.y..end.y))

    fun center() = Coords(x / 2, y / 2)

    fun xCenter() = Coords(x / 2, y)

    fun yCenter() = Coords(x, y / 2)
}

infix fun String.centeredAt(pos: Coords) = box.centeredAt(pos)

fun String.centeredAt(x: Int, y: Int) = centeredAt(Coords(x, y))

infix fun Coords.centeredAt(pos: Coords) = pos - center()

fun Coords.centeredAt(x: Int, y: Int) = centeredAt(Coords(x, y))

val String.box: Coords
    get() = Minecraft.getMinecraft().fontRendererObj.run {
        Coords(getStringWidth(this@box), FONT_HEIGHT)
    }