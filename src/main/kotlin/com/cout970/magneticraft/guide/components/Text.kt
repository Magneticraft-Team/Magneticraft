package com.cout970.magneticraft.guide.components

import coffee.cypher.mcextlib.strings.i18n
import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.guide.FONT_HEIGHT
import com.cout970.magneticraft.gui.client.guide.GuiGuideBook
import com.cout970.magneticraft.gui.client.guide.GuiPageComponent
import com.cout970.magneticraft.guide.GUIDE
import com.cout970.magneticraft.guide.LinkInfo
import com.cout970.magneticraft.guide.Page
import net.minecraft.client.resources.I18n

class Text(
    position: Coords,
    override val size: Coords,
    text: String
) : PageComponent(position) {
    val words = parseText(text)

    override fun toGuiComponent(parent: Page.Gui): GuiPageComponent = TextComponentGui(parent)

    private inner class TextComponentGui(parent: Page.Gui) : Gui(parent) {
        lateinit var boxes: List<TextBox>

        override fun initGui() {
            super.initGui()

            placeBoxes()
        }

        fun placeBoxes() {
            var x = 0
            var y = 0;
            val boxList = mutableListOf<TextBox>()

            val space = parent.gui.getCharWidth(' ')

            for ((word, link) in words) {
                val width = parent.gui.getStringWidth(word)
                if (width > size.x) {
                    throw IllegalStateException(
                        "Word $word is larger than text box. Increase text box width or change the word."
                    )
                }

                if (x + width > size.x) {
                    x = 0
                    y += FONT_HEIGHT + 1
                    if (y > size.y) {
                        throw IllegalStateException("Text is larger than the text box.")
                    }
                } else {
                    if (boxList.isNotEmpty() && boxList.last().link == link) {
                        boxList.last().nextLink = true
                    }
                }

                if (x + space > size.x) {
                    boxList += TextBox(this, Coords(x, y), word, false, link)
                } else {
                    boxList += TextBox(this, Coords(x, y), word, true, link)
                    x += space
                }

                x += width
            }

            boxes = boxList
        }

        override fun draw(mouse: Coords, time: Double) {
            boxes.forEach { it.draw(mouse) }
        }

        override fun postDraw(mouse: Coords, time: Double) {
            boxes.forEach { it.postDraw(mouse) }
        }

        override fun onLeftClick(mouse: Coords): Boolean {
            val box = boxes.firstOrNull { it.isInside(mouse) && it.link != null }

            box ?: return false
            box.link ?: return false

            parent.gui.mc.displayGuiScreen(GuiGuideBook(box.link.getEntryTarget()))

            return true
        }
    }

    private class TextBox(
        val parent: Gui,
        val position: Coords,
        val text: String,
        val space: Boolean,
        val link: LinkInfo? = null
    ) {
        val page = parent.parent
        val size = Coords(page.gui.getStringWidth(text), FONT_HEIGHT)
        var nextLink = false

        val drawPos: Coords
            get() = parent.drawPos + position

        fun isInside(pos: Coords) = pos.inside(drawPos, drawPos + size)

        fun draw(mouse: Coords) {
            val prefix = if (link == null) {
                "§r"
            } else {
                if (isInside(mouse)) {
                    "§9§n"
                } else {
                    "§r§n"
                }
            }

            val spaceFormat = if (space) {
                if (nextLink) " " else "§r "
            } else {
                ""
            }

            page.gui.drawString(
                pos = drawPos,
                text = prefix + text + spaceFormat
            )
        }

        fun postDraw(mouse: Coords) {
            if (link != null && isInside(mouse)) {
                page.gui.drawHoveringText(listOf(I18n.format("$GUIDE.link.text", link.entry.i18n(), link.page + 1)), mouse)
            }
        }
    }
}

fun parseText(text: String): List<Pair<String, LinkInfo?>> {
    if (!text.contains('[')) {
        return text.split(' ').filter(String::isNotBlank).map { it to null }
    }

    //before first link
    val prefix = parseText(text.substringBefore('['))
    val remText = text.substringAfter('[')

    //link text [<this part>](...)
    val linkWords = parseText(remText.substringBefore(']'))
    val remainder = remText.substringAfter("](")

    //link target [...](<this part>)
    val link = remainder.substringBefore(')').split(':').run {
        LinkInfo(get(0).trim(), get(1).trim().toInt())
    }

    //recurse for rest of the text
    return prefix + linkWords.map { it.first to link } + parseText(remainder.substringAfter(')'))
}