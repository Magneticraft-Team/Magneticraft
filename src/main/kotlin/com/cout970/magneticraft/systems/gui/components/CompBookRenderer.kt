package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.logError
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.manual.*
import net.minecraft.client.renderer.GlStateManager
import java.util.*

/**
 * Created by cout970 on 2017/08/03.
 */
class CompBookRenderer : IComponent {

    companion object {
        val backgroundTexture = resource("textures/gui/guide/book_alt.png")
        val backgroundSize: IVector2 = vec2Of(280, 180)
        val scale get() = Config.guideBookScale

        var book: Book = loadBook()
        var currentSection: String = "index"
        var pageIndex = 0
        val sectionHistory = ArrayDeque<String>()
        val undoHistory = ArrayDeque<String>()
    }

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = vec2Of(280, 186) * scale
    override lateinit var gui: IGui

    // Processed pages to be rendered
    var pages: List<Page> = emptyList()

    // Offset form the left-top corner
    val textOffset get() = vec2Of(14, 14) * scale

    // Size of bounding box of the page, text cannot be placed outside this box
    val pageSize get() = vec2Of(250 * scale, scale * 148)

    override fun init() {
        openSection()
    }

    fun openPage(section: String, page: Int) {
        currentSection = section
        pageIndex = page
        openSection()
    }

    fun goBack() {
        if (sectionHistory.size <= 1) return
        undoHistory.push(sectionHistory.pop())
        currentSection = sectionHistory.peek()
        pageIndex = 0
        loadPages()
    }

    fun goForward() {
        if (undoHistory.isEmpty()) return
        currentSection = undoHistory.pop()
        sectionHistory.push(currentSection)
        pageIndex = 0
        loadPages()
    }

    fun openSection() {
        sectionHistory.push(currentSection)
        undoHistory.clear()
        loadPages()
    }

    fun loadPages() {
        val doc = book.sections[currentSection]?.document ?: errorDocument()

        pages = MdRenderer.render(doc, pageSize, gui.fontHelper.FONT_HEIGHT, gui.fontHelper::getStringWidth)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        GlStateManager.color(1f, 1f, 1f)
        gui.bindTexture(backgroundTexture)
        gui.drawTexture(gui.pos, size, Vec2d.ZERO, backgroundSize, vec2Of(512))

        if (currentSection != "index") {
            renderArrow(Arrow.INDEX, mouse in arrowHitbox(Arrow.INDEX))
        }

        if (pageIndex >= 1) {
            renderArrow(Arrow.LEFT, mouse in arrowHitbox(Arrow.LEFT))
        }

        if (pageIndex + 1 in pages.indices) {
            renderArrow(Arrow.RIGHT, mouse in arrowHitbox(Arrow.RIGHT))
        }

        if (pageIndex in pages.indices) {
            renderPage(pages[pageIndex], mouse, gui.pos + textOffset)
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {

        if (pageIndex - 1 in pages.indices && mouse in arrowHitbox(Arrow.LEFT)) {
            pageIndex--
            return true
        }

        if (pageIndex + 1 in pages.indices && mouse in arrowHitbox(Arrow.RIGHT)) {
            pageIndex++
            return true
        }

        if (currentSection != "index" && mouse in arrowHitbox(Arrow.INDEX)) {
            sectionHistory.clear()
            openPage("index", 0)
        }

        if (pageIndex in pages.indices) {
            val link = checkLinkClick(pages[pageIndex], mouse, gui.pos + textOffset)
            if (link != null) {
                if (link.linkSection in book.sections) {
                    openPage(link.linkSection, link.linkPage)
                } else {
                    logError("Unable to open page: {}", link.linkSection)
                }
            }
        }

        return false
    }

    override fun onKeyTyped(typedChar: Char, keyCode: Int): Boolean {
        when (keyCode) {
            14 -> goBack()
            28 -> goForward()
            205 -> if (pageIndex + 1 in pages.indices) pageIndex++ // Right
            203 -> if (pageIndex - 1 in pages.indices) pageIndex-- // Left
            19 -> {
                book = loadBook()
                loadPages()
            }
            else -> return false
        }
        return true
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
        val (pos, size) = arrowHitbox(it)

        gui.drawTexture(DrawableBox(pos, size, uv.first, uv.second, vec2Of(512)))
    }

    fun renderPage(page: Page, mouse: IVector2, pos: IVector2) {
        page.text.forEach {
            renderTextBox(it, pos, 0x303030)
        }
        page.links.forEach { link ->
            val color = if (link.contains(mouse, gui, pos)) 0x0b99ff else 0x1d3fee
            link.words.forEach { renderTextBox(it, pos, color) }
        }
    }

    fun renderTextBox(it: TextBox, pos: IVector2, color: Int) {
        gui.drawShadelessString(
                text = it.txt,
                pos = pos + it.pos,
                color = color
        )
    }

    fun arrowHitbox(arrow: Arrow): Pair<IVector2, IVector2> {
        val pos = when (arrow) {
            Arrow.LEFT -> vec2Of(20 * scale, 178 * scale)
            Arrow.RIGHT -> vec2Of(242 * scale, 178 * scale)
            Arrow.INDEX -> vec2Of(130 * scale, 178 * scale)
        }
        return (pos + gui.pos) to (arrow.size * scale)
    }

    enum class Arrow(val size: IVector2 = vec2Of(18, 26),
                     val uvSize: IVector2 = vec2Of(18, 26),
                     val uvPos: IVector2,
                     val hoverUv: IVector2) {

        LEFT(uvPos = vec2Of(40, 180), hoverUv = vec2Of(60, 180)),
        RIGHT(uvPos = vec2Of(80, 180), hoverUv = vec2Of(100, 180)),
        INDEX(uvPos = vec2Of(0, 180), hoverUv = vec2Of(20, 180));
    }
}