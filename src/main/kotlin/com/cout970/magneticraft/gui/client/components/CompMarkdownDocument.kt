package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.gui.client.guide.*
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.offset
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.text.TextFormatting

/**
 * Created by cout970 on 2017/08/03.
 */
class CompBookRenderer : IComponent {

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = vec2Of(280, 186)
    override lateinit var gui: IGui

    companion object {
        val BACKGROUND = resource("textures/gui/guide/book.png")
        //        val book: Book by lazy { loadBook() }
        val book: Book get() = loadBook()
        val textOffset = vec2Of(22, 24)

        var currentSection: String = "index"
        var pageIndex = 0
        var scale = 1.0
    }

    var pages: List<Page> = emptyList()
    val pageSize get() = vec2Of(115 * scale, scale * 130)
    val pageOffset get() = 130 * scale

    override fun init() {
        openSection()
    }

    fun openPage(section: String, page: Int) {
        currentSection = section
        pageIndex = page
        openSection()
    }

    fun openSection() {
        val section = book.sections[currentSection] ?: Section("empty", MarkdownDocument(emptyList()))
        val doc = section.document

        pages = doc.mapToPages()
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(BACKGROUND)
        gui.drawTexture(DrawableBox(gui.pos to size, Vec2d.ZERO to size, vec2Of(512)))

        if (currentSection != "index") {
            renderArrow(Arrow.INDEX, mouse in Arrow.INDEX.collisionBox.offset(gui.pos))
        }
        if (pageIndex >= 2)
            renderArrow(Arrow.LEFT, mouse in Arrow.LEFT.collisionBox.offset(gui.pos))
        if (pageIndex + 2 in pages.indices)
            renderArrow(Arrow.RIGHT, mouse in Arrow.RIGHT.collisionBox.offset(gui.pos))

        GlStateManager.pushMatrix()
        GlStateManager.translate(gui.pos.x + textOffset.xi, gui.pos.y + textOffset.yi, 0.0)
        GlStateManager.scale(scale, scale, 1.0)
        if (pageIndex in pages.indices) {
            renderPage(pages[pageIndex], vec2Of(0, 0))
        }
        if (pageIndex + 1 in pages.indices) {
            renderPage(pages[pageIndex + 1], vec2Of(pageOffset, 0))
        }
        GlStateManager.popMatrix()

        if (pageIndex in pages.indices) {
            checkLinkClick(pages[pageIndex], mouse, textOffset)
        }
        if ((pageIndex + 1) in pages.indices) {
            checkLinkClick(pages[pageIndex + 1], mouse, vec2Of(pageOffset, 0) + textOffset)
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {

        if (pageIndex >= 2 && mouse in Arrow.LEFT.collisionBox.offset(gui.pos)) {
            pageIndex -= 2
            return true
        }
        if (pageIndex + 2 in pages.indices && mouse in Arrow.RIGHT.collisionBox.offset(gui.pos)) {
            pageIndex += 2
            return true
        }

        if (currentSection != "index" && mouse in Arrow.INDEX.collisionBox.offset(gui.pos)) {
            openPage("index", 0)
        }

        if (pageIndex in pages.indices) {
            var link = checkLinkClick(pages[pageIndex], mouse, textOffset)
            if (link == null && (pageIndex + 1) in pages.indices) {
                link = checkLinkClick(pages[pageIndex + 1], mouse, vec2Of(pageSize.xi, 0) + textOffset)
            }
            if (link != null) {
                if (link.linkSection in book.sections)
                    openPage(link.linkSection, link.linkPage)
            }
        }

        return false
    }

    fun checkLinkClick(page: Page, mouse: Vec2d, offset: IVector2): LinkTextBox? {
        page.links.forEach {
            if (mouse in (gui.pos + (it.pos * scale) + offset to it.size())) {
                return it
            }
        }
        return null
    }


    fun TextBox.size(): IVector2 {
        return vec2Of(gui.fontHelper.getStringWidth(txt + " ") * scale, gui.fontHelper.FONT_HEIGHT * scale)
    }

    fun renderArrow(it: Arrow, hover: Boolean) {
        val uv = if (hover) it.hoverUv to it.size else it.uvPos to it.size
        gui.drawTexture(DrawableBox(gui.pos + it.pos to it.size, uv, vec2Of(512)))
    }

    fun MarkdownDocument.mapToPages(): List<Page> {
        val ctx = Context()
        val txt = root.flatMap { it.mapToText(ctx) }
        return txt.groupBy { it.page }.map {
            Page(
                    text = it.value.filterIsInstance<NormalTextBox>(),
                    links = it.value.filterIsInstance<LinkTextBox>(),
                    index = it.key
            )
        }
    }

    fun Context.newLine() {
        lastPosY += gui.fontHelper.FONT_HEIGHT
        lastPosX = 0
        if (lastPosY > pageSize.yi) {
            lastPosY = 0
            page++
        }
    }

    fun MarkdownTag.mapToText(ctx: Context): List<TextBox> {
        when (this) {
            is MarkdownText -> {
                if (txt.isEmpty()) return emptyList()

                val list = mutableListOf<TextBox>()

                if (txt.length != 1 || txt != "\n") {
                    txt.split(" ", "\n").filter { it.isNotEmpty() }.forEach {

                        val size = gui.fontHelper.getStringWidth(ctx.prefix + it + " ")

                        if (ctx.lastPosX + size > pageSize.xi) {
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
            is MarkdownNewLine -> {
                ctx.newLine()
            }
            is MarkdownLink -> {
                val (linkSection, page) = parseUrl(url)
                return childs.flatMap { it.mapToText(ctx) }.map {
                    LinkTextBox(it.txt, it.pos, it.page, linkSection, page)
                }
            }
            is MarkdownItalic -> {
                ctx.prefix += TextFormatting.ITALIC
                val ret = childs.flatMap { it.mapToText(ctx) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                return ret
            }
            is MarkdownBold -> {
                ctx.prefix += TextFormatting.BOLD
                val ret = childs.flatMap { it.mapToText(ctx) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                return ret
            }
            is MarkdownHeader -> {
                ctx.prefix += TextFormatting.BOLD
                val ret = childs.flatMap { it.mapToText(ctx) }
                ctx.prefix = ctx.prefix.substring(0, ctx.prefix.length - 2)
                return ret
            }
        }
        return emptyList()
    }

    fun parseUrl(url: String): Pair<String, Int> {
        val separator = url.indexOfLast { it == '#' }

        val page = if (separator != -1) {
            url.substringAfterLast('#').toIntOrNull() ?: 0
        } else 0

        val urlWithoutPage = if (separator != -1) {
            url.substringBeforeLast('#')
        } else url

        val slashIndex = urlWithoutPage.indexOfLast { it == '/' }

        val section = if (slashIndex == -1) {
            urlWithoutPage
        } else urlWithoutPage.substringAfterLast('/')

        return section to page
    }

    fun renderPage(page: Page, pos: IVector2) {
        page.text.forEach {
            renderTextBox(it, pos)
        }
        page.links.forEach {
            renderTextBox(it, pos)
        }
    }

    fun renderTextBox(it: TextBox, pos: IVector2) {
        gui.drawShadelessString(
                text = it.txt,
                pos = pos + it.pos,
                color = 0xAFAFAF
        )
        gui.drawShadelessString(
                text = it.txt,
                pos = pos + it.pos,
                color = if (it is LinkTextBox) 0x3030F0 else 0x303030
        )
    }

    data class Page(val text: List<NormalTextBox>, val links: List<LinkTextBox> = emptyList(), val index: Int)

    abstract class TextBox(val txt: String, val pos: IVector2, val page: Int)

    class NormalTextBox(txt: String, pos: IVector2, page: Int) : TextBox(txt, pos, page)

    class LinkTextBox(
            txt: String,
            pos: IVector2,
            page: Int,
            val linkSection: String,
            val linkPage: Int
    ) : TextBox(txt, pos, page)

    data class Context(
            var lastPosX: Int = 0,
            var lastPosY: Int = 0,
            var prefix: String = "",
            var page: Int = 0
    )

    enum class Arrow(val pos: IVector2, val size: IVector2 = vec2Of(18, 26), val uvPos: IVector2,
                     val hoverUv: IVector2) {

        LEFT(pos = vec2Of(30, 164), uvPos = vec2Of(26, 188), hoverUv = vec2Of(26, 218)),
        RIGHT(pos = vec2Of(232, 164), uvPos = vec2Of(4, 188), hoverUv = vec2Of(4, 218)),
        INDEX(pos = vec2Of(98, 172), uvPos = vec2Of(4, 247), hoverUv = vec2Of(26, 247));

        val collisionBox = pos to size
    }
}