package com.cout970.magneticraft.systems.manual

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of
import net.minecraft.util.text.TextFormatting

object MdRenderer {

    fun render(doc: MarkdownDocument, pageSize: IVector2, fontHeight: Int, fontWidth: (String) -> Int): List<Page> {
        val ctx = Context(pageSize, fontHeight, fontWidth, location = doc.location)
        val txt = doc.root.flatMap { renderTag(ctx, it) }

        return txt.groupBy { it.page }.map {
            Page(
                text = it.value.filterIsInstance<NormalTextBox>(),
                links = it.value.filterIsInstance<LinkTextBox>(),
                index = it.key
            )
        }
    }

    fun renderTag(ctx: Context, tag: MdTag): List<TextBox> = tag.run {
        when (this) {
            is MdBr -> renderText(ctx, "\n")
            is MdText -> {
                if (txt.isEmpty()) {
                    emptyList()
                } else {
                    renderText(ctx, txt)
                }
            }
            is MdLink -> {
                val (linkSection, page) = parseUrl(ctx.location, url)

                listOf(LinkTextBox(childs.flatMap { renderTag(ctx, it) }, linkSection, page))
            }
            is MdItalic -> {
                ctx.prefix += TextFormatting.ITALIC
                val ret = childs.flatMap { renderTag(ctx, it) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                ret
            }
            is MdBold -> {
                ctx.prefix += TextFormatting.BOLD
                val ret = childs.flatMap { renderTag(ctx, it) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                ret
            }
            is MdHeader -> {
                ctx.prefix += TextFormatting.BOLD
                val ret = childs.flatMap { renderTag(ctx, it) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                ret
            }
            is MdNewLine -> {
                ctx.newLine()
                emptyList()
            }
            is MdListItem -> {
                val children = childs.flatMap { renderTag(ctx, it) }
                ctx.newLine()
                children
            }
            is MdUnsortedList -> {
                ctx.newLine()
                childs.flatMap { renderText(ctx, "* ") + renderTag(ctx, it) }
            }
            else -> emptyList()
        }
    }

    private fun renderText(ctx: Context, txt: String): List<TextBox> {
        val list = mutableListOf<TextBox>()

        if (txt.length != 1 || txt != "\n") {
            txt.split(" ", "\n").filter { it.isNotEmpty() }.forEach {

                val size = ctx.fontWidth(ctx.prefix + it + " ")

                if (ctx.lastPosX + size > ctx.pageSize.xi) {
                    ctx.newLine()
                }

                list += NormalTextBox(ctx.prefix + it, vec2Of(ctx.lastPosX, ctx.lastPosY), ctx.page)
                ctx.lastPosX += size
            }
        }

        if (txt.endsWith("\n")) {
            ctx.newLine()
        }
        return list
    }

    fun parseUrl(location: String, url: String): Pair<String, Int> {
        val separator = url.indexOfLast { it == '#' }

        val page = if (separator != -1) {
            url.substringAfterLast('#').toIntOrNull() ?: 0
        } else 0

        var urlWithoutPage = if (separator != -1) url.substringBeforeLast('#') else url
        var baseLoc = location

        while (urlWithoutPage.startsWith("../")) {
            val parentIndex = baseLoc.lastIndexOf('/')
            val index = urlWithoutPage.lastIndexOf("../")

            baseLoc = if (parentIndex == -1) "" else baseLoc.substring(0, parentIndex)
            urlWithoutPage = urlWithoutPage.replaceRange(index, index + 3, "")
        }

        if (baseLoc.isNotEmpty()) {
            baseLoc += '/'
        }

        // we remove the ".md" suffix of the page Url because
        // with .md suffix, in github we can follow the links in the formatted Markdown
        // in game we register resources without ".md" suffixes (see removeSuffix(...) call in Chapters#loadBook)
        val section = baseLoc + urlWithoutPage.removeSuffix(".md")

        return section to page
    }

    data class Context(
        val pageSize: IVector2,
        val fontHeight: Int,
        val fontWidth: (String) -> Int,
        var lastPosX: Int = 0,
        var lastPosY: Int = 0,
        var prefix: String = "",
        var page: Int = 0,
        var location: String = ""
    ) {

        fun newLine() {
            lastPosY += fontHeight + 2
            lastPosX = 0
            if (lastPosY > pageSize.yi) {
                lastPosY = 0
                page++
            }
        }
    }
}
