package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.config.Config
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

/**
 * Created by cout970 on 2017/08/03.
 */
class CompBookRenderer : IComponent {

    companion object {
        val BACKGROUND = resource("textures/gui/guide/book.png")
        val backgroundSize: IVector2 = vec2Of(280, 186)
        val book: Book by lazy { loadBook() }
        var currentSection: String = "index"
        var pageIndex = 0
        val scale get() = Config.guideBookScale
    }

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = vec2Of(280, 186) * scale
    override lateinit var gui: IGui

    var pages: List<Page> = emptyList()
    val pageSize get() = vec2Of(110 * scale, scale * 130)
    val pageOffset get() = 130 * scale
    val textOffset get() = vec2Of(22, 24) * scale

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

        pages = MdRenderer.render(doc, pageSize, gui.fontHelper.FONT_HEIGHT, gui.fontHelper::getStringWidth)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f)
        gui.bindTexture(BACKGROUND)
        gui.drawTexture(DrawableBox(gui.pos, size, Vec2d.ZERO, backgroundSize, vec2Of(512)))

        if (currentSection != "index") {
            renderArrow(Arrow.INDEX, mouse in Arrow.INDEX.collisionBox.offset(gui.pos))
        }
        if (pageIndex >= 2)
            renderArrow(Arrow.LEFT, mouse in Arrow.LEFT.collisionBox.offset(gui.pos))
        if (pageIndex + 2 in pages.indices)
            renderArrow(Arrow.RIGHT, mouse in Arrow.RIGHT.collisionBox.offset(gui.pos))

        if (pageIndex in pages.indices) {
            renderPage(pages[pageIndex], mouse, gui.pos + textOffset)
        }
        if (pageIndex + 1 in pages.indices) {
            renderPage(pages[pageIndex + 1], mouse, vec2Of(pageOffset, 0) + gui.pos + textOffset)
        }

        if (pageIndex in pages.indices) {
            checkLinkClick(pages[pageIndex], mouse, gui.pos + textOffset)
        }
        if ((pageIndex + 1) in pages.indices) {
            checkLinkClick(pages[pageIndex + 1], mouse, vec2Of(pageOffset, 0) + gui.pos + textOffset)
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
            var link = checkLinkClick(pages[pageIndex], mouse, gui.pos + textOffset)
            if (link == null && (pageIndex + 1) in pages.indices) {
                link = checkLinkClick(pages[pageIndex + 1], mouse, vec2Of(pageOffset, 0) + gui.pos + textOffset)
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
            if (it.contains(mouse, gui, offset)) {
                return it
            }
        }
        return null
    }

    fun renderArrow(it: Arrow, hover: Boolean) {
        val uv = if (hover) it.hoverUv to it.uvSize else it.uvPos to it.uvSize
        gui.drawTexture(DrawableBox(gui.pos + it.pos, it.size, uv.first, uv.second, vec2Of(512)))
    }


    fun renderPage(page: Page, mouse: IVector2, pos: IVector2) {
        page.text.forEach {
            renderTextBox(it, pos, 0x303030)
        }
        page.links.forEach {
            val color = if (it.contains(mouse, gui, pos)) 0x0b99ff else 0x1d3fee
            it.words.forEach { renderTextBox(it, pos, color) }
        }
    }

    fun renderTextBox(it: TextBox, pos: IVector2, color: Int) {

        gui.drawShadelessString(
                text = it.txt,
                pos = pos + it.pos,
                color = color
        )
    }

    enum class Arrow(val pos: IVector2, val size: IVector2 = vec2Of(18, 26) * scale,
                     val uvSize: IVector2 = vec2Of(18, 26),
                     val uvPos: IVector2,
                     val hoverUv: IVector2) {

        LEFT(pos = vec2Of(30 * scale, 164 * scale), uvPos = vec2Of(26, 188), hoverUv = vec2Of(26, 218)),
        RIGHT(pos = vec2Of(232 * scale, 164 * scale), uvPos = vec2Of(4, 188), hoverUv = vec2Of(4, 218)),
        INDEX(pos = vec2Of(98 * scale, 172 * scale), uvPos = vec2Of(4, 247), hoverUv = vec2Of(26, 247));

        val collisionBox = pos to size
    }
}