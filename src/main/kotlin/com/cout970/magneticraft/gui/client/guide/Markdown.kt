package com.cout970.magneticraft.gui.client.guide

/**
 * Created by cout970 on 2017/08/03.
 */

private val header = """(#+)(.*)""".toRegex()
private val strong = """\*\*\*(.*)\*\*\*""".toRegex()
private val bold = """\*\*(.*)\*\*""".toRegex()
private val italic = """\*(.*)\*""".toRegex()
private val link = """\[([^\]]*)\]\(([^)]*)\)""".toRegex()

data class MarkdownDocument(val root: List<MdTag>)

abstract class MdTag {
    abstract val childs: List<MdTag>
}

abstract class MdEndTag : MdTag() {
    override val childs: List<MdTag> = emptyList()
}

data class MdHeader(val level: Int, override val childs: List<MdTag>) : MdTag()

data class MdItalic(override val childs: List<MdTag>) : MdTag()

data class MdBold(override val childs: List<MdTag>) : MdTag()

data class MdStrikethrough(override val childs: List<MdTag>) : MdTag()

data class MdSortedList(override val childs: List<MdTag>) : MdTag()

data class MdUnsortedList(override val childs: List<MdTag>) : MdTag()

data class MdLink(val url: String, override val childs: List<MdTag>) : MdTag()

data class MdImage(val url: String, val alt: String) : MdEndTag()

class MdHorizontalRule : MdEndTag()

class MdNewLine : MdEndTag()

data class MdText(val txt: String) : MdEndTag() {
    override fun toString(): String {
        return "MdText(txt='${txt.replace("\n", "\\n")}')"
    }
}

object MdBr : MdEndTag()


fun parseChildren(str: String): List<MdTag> {
    if (str.isEmpty()) {
        return emptyList()
    }

    if (str == "\n") {
        return listOf(MdBr)
    }

    val lines = if (str.contains("\n")) str.lines().map { it + "\n" } else listOf(str)
    //    val lines = str.lines().flatMap { if (it.isBlank()) listOf("\n", "\n") else listOf(it) }

    if (lines.isEmpty()) {
        return emptyList()
    }

    val children = mutableListOf<MdTag>()

    lines.forEach { line ->
        if (line.startsWith("#")) {
            val level = line.takeWhile { it == '#' }.length
            if (line.length > level && line[level] == ' ') {
                children += MdHeader(level, parseChildren(line.replace(header, "$2")))
                return@forEach
            }
        }

        if (line == "---" || line == "===") {
            children += MdHorizontalRule()
            return@forEach
        }

        if (line.contains(strong)) {
            val a = line.indexOfFirst { it == '*' }
            val b = line.indexOfLast { it == '*' }
            val start = line.substring(0, a)
            val middle = line.substring(a + 3, b - 2)
            val end = line.substring(b + 1, line.length)

            children += parseChildren(start)
            children += MdBold(parseChildren(middle))
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
            children += MdBold(parseChildren(middle))
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
            children += MdBold(parseChildren(middle))
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
            children += MdLink(address, parseChildren(text))
            children += parseChildren(postText)
            return@forEach
        }

        children += MdText(line)
    }
    return children
}