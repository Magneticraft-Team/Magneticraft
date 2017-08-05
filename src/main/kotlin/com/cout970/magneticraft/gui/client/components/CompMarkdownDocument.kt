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
        val book: Book by lazy { Book() }
    }

    var currentSection: String = book.sections.keys.first()
    var pageIndex = 0
    var pages: List<Page> = emptyList()

    init {
        openSection()
    }

    fun openSection() {
        val section = book.sections[currentSection]!!
        val doc = section.document

        pages = doc.mapToPages()
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(BACKGROUND)
        gui.drawTexture(DrawableBox(gui.pos to size, Vec2d.ZERO to size, vec2Of(512)))

        if (pageIndex >= 2)
            renderArrow(Arrow.LEFT, mouse in Arrow.LEFT.collisionBox.offset(gui.pos))
        if (pageIndex + 2 in pages.indices)
            renderArrow(Arrow.RIGHT, mouse in Arrow.RIGHT.collisionBox.offset(gui.pos))

        GlStateManager.pushMatrix()
        GlStateManager.translate(gui.pos.x + 22, gui.pos.y + 22, 0.0)
        GlStateManager.scale(2 / 3.0, 2 / 3.0, 1.0)
        renderPage(pages[pageIndex], vec2Of(0, 0))
        if (pageIndex + 1 in pages.indices) {
            renderPage(pages[pageIndex + 1], vec2Of(192, 0))
        }
        GlStateManager.popMatrix()
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
        return super.onMouseClick(mouse, mouseButton)
    }

    data class Page(val text: List<TextBox>, val links: List<LinkTextBox> = emptyList(), val index: Int)

    open class TextBox(val txt: String, val pos: IVector2, val page: Int)

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

        LEFT(pos = Vec2d(30, 164), uvPos = vec2Of(4, 188), hoverUv = vec2Of(4, 218)),
        RIGHT(pos = Vec2d(232, 164), uvPos = vec2Of(26, 188), hoverUv = vec2Of(26, 218));

        val collisionBox = pos to size
    }

    fun renderArrow(it: Arrow, hover: Boolean) {
        val uv = if (hover) it.hoverUv to it.size else it.uvPos to it.size
        gui.drawTexture(DrawableBox(gui.pos + it.pos to it.size, uv, vec2Of(512)))
    }

    fun MarkdownDocument.mapToPages(): List<Page> {
        val ctx = Context()
        val txt = root.flatMap { it.mapToText(ctx) }
        return txt.groupBy { it.page }.map { Page(it.value.toList(), index = it.key) }
    }

    fun MarkdownTag.mapToText(ctx: Context): List<TextBox> {
        when (this) {
            is MarkdownText -> {
                if (txt.isEmpty()) return emptyList()

                val list = mutableListOf<TextBox>()

                if (txt.length != 1 || txt != "\n") {
                    txt.split(" ", "\n").filter { it.isNotEmpty() }.forEach {

                        val size = gui.fontHelper.getStringWidth(ctx.prefix + it + " ")

                        if (ctx.lastPosX + size > 170) {
                            ctx.lastPosX = 0
                            ctx.lastPosY += gui.fontHelper.FONT_HEIGHT
                            if (ctx.lastPosY > 195) {
                                ctx.lastPosY = 0
                                ctx.page++
                            }
                        }

                        list += TextBox(ctx.prefix + it, vec2Of(ctx.lastPosX, ctx.lastPosY), ctx.page)
                        ctx.lastPosX += size
                    }
                }

                if (txt.endsWith("\n")) {
                    ctx.lastPosY += gui.fontHelper.FONT_HEIGHT
                    ctx.lastPosX = 0
                }
                return list
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
                return childs.flatMap { it.mapToText(ctx) }
            }
        }
        return emptyList()
    }

    fun renderPage(page: Page, pos: IVector2) {
        page.text.forEach {
            gui.drawShadelessString(
                    text = it.txt,
                    pos = pos + it.pos,
                    color = 0xAFAFAF
            )
            gui.drawShadelessString(
                    text = it.txt,
                    pos = pos + it.pos,
                    color = 0x303030
            )
        }
    }
}