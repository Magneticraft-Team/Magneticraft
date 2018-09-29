package com.cout970.magneticraft.systems.manual

/**
 * Created by cout970 on 2017/08/03.
 */

data class MarkdownDocument(val root: List<MdTag>, val location: String)

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

data class MdUnsortedList(override val childs: List<MdListItem>) : MdTag()

data class MdListItem(override val childs: List<MdTag>) : MdTag()

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

fun parseMarkdownDocument(str: String, loc: String): MarkdownDocument {
    val doc = MarkdownParser(str).parse()
    val tags = doc.paragraphs.flatMap { it.toTags() }

    return MarkdownDocument(tags, loc)
}

private fun Paragraph.toTags(): List<MdTag> {
    val tags = lines.flatMap { it.toTags() }

    return tags + MdBr + MdBr
}

private fun Line.toTags(): List<MdTag> = content.map { it.toTags() }

private fun LineContent.toTags(): MdTag {
    return when (this) {
        is Text -> MdText(txt)
        is Bold -> MdBold(listOf(MdText(txt)))
        is Italic -> MdItalic(listOf(MdText(txt)))
        is BoldItalic -> MdBold(listOf(MdItalic(listOf(MdText(txt)))))
        is Header -> MdHeader(level, content.map { it.toTags() })
        is Link -> MdLink(url, listOf(MdText(txt)))
        is ListItem -> MdListItem(content.map { it.toTags() })
        is ListGroup -> MdUnsortedList(items.map { MdListItem(it.content.map { it.toTags() }) })
    }
}