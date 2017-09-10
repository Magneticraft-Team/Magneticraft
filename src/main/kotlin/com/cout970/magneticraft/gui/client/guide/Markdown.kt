package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.client.gui.FontRenderer

/**
 * Created by cout970 on 2017/08/03.
 */

private val header = """(#+)(.*)""".toRegex()
private val strong = """\*\*\*(.*)\*\*\*""".toRegex()
private val bold = """\*\*(.*)\*\*""".toRegex()
private val italic = """\*(.*)\*""".toRegex()
private val link = """\[([^\]]*)\]\(([^)]*)\)""".toRegex()

//[I'm an inline-style link](https://www.google.com)


fun parseChildren(str: String): List<MarkdownTag> {
    if (str.isEmpty()) return emptyList()
    val lines = if (str.contains("\n")) str.lines().map { it + "\n" } else listOf(str)
    if (lines.isEmpty()) return emptyList()
    val children = mutableListOf<MarkdownTag>()

    lines.forEach { line ->
        if (line.startsWith("#")) {
            val level = line.takeWhile { it == '#' }.length
            if (line.length > level && line[level] == ' ') {
                children += MarkdownHeader(level, parseChildren(line.replace(header, "$2")))
                return@forEach
            }
        }
        if (line == "---" || line == "===") {
            children += MarkdownHorizontalRule()
            return@forEach
        }
        if (line.contains(strong)) {
            val a = line.indexOfFirst { it == '*' }
            val b = line.indexOfLast { it == '*' }
            val start = line.substring(0, a)
            val middle = line.substring(a + 3, b - 2)
            val end = line.substring(b + 1, line.length)

            children += parseChildren(start)
            children += MarkdownBold(parseChildren(middle))
            children += parseChildren(end)
            return@forEach
        }
        if (line.contains(bold)) {
            val a = line.indexOfFirst { it == '*' }
            val b = line.indexOfLast { it == '*' }
            val start = line.substring(0, a)
            val middle = line.substring(a + 2, b - 1)
            val end = line.substring(b + 1, line.length)

            children += parseChildren(start)
            children += MarkdownBold(parseChildren(middle))
            children += parseChildren(end)
            return@forEach
        }
        if (line.contains(italic)) {
            val a = line.indexOfFirst { it == '*' }
            val b = line.indexOfLast { it == '*' }
            val start = line.substring(0, a)
            val middle = line.substring(a + 1, b)
            val end = line.substring(b + 1, line.length)

            children += parseChildren(start)
            children += MarkdownBold(parseChildren(middle))
            children += parseChildren(end)
            return@forEach
        }

        if (line.contains(link)) {
            val match = link.find(line)!!
            val preText = line.substring(0 until match.range.start)
            val text = match.groupValues[1]
            val postText = line.substring(match.range.endInclusive + 1 until line.length)
            val address = match.groupValues[2]

            children += parseChildren(preText)
            children += MarkdownLink(address, parseChildren(text))
            children += parseChildren(postText)
            return@forEach
        }

        children += MarkdownText(line)
        return@forEach
    }
    return children
}

data class MarkdownDocument(val root: List<MarkdownTag>)

abstract class MarkdownTag {
    abstract val childs: List<MarkdownTag>
}

data class MarkdownHeader(val level: Int, override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownItalic(override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownBold(override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownStrikethrough(override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownSortedList(override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownUnsortedList(override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownLink(val url: String, override val childs: List<MarkdownTag>) : MarkdownTag()
data class MarkdownImage(val url: String, val alt: String) : MarkdownTag() {
    override val childs: List<MarkdownTag> = emptyList()
}

class MarkdownHorizontalRule : MarkdownTag() {
    override val childs: List<MarkdownTag> = emptyList()
}

class MarkdownNewLine : MarkdownTag() {
    override val childs: List<MarkdownTag> = emptyList()
}

data class MarkdownText(val txt: String) : MarkdownTag() {
    override val childs: List<MarkdownTag> = emptyList()

    fun getSize(fontRenderer: FontRenderer) = vec2Of(fontRenderer.getStringWidth(txt), fontRenderer.FONT_HEIGHT)
}
