package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.gui.client.GuiCommon
import com.cout970.magneticraft.guide.BookEntry
import com.cout970.magneticraft.guide.BookPage.Gui
import com.cout970.magneticraft.guide.book
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

val BOOK = resource("textures/gui/guide/book.png")
val ARROW_SIZE = Vec2d(18, 26)
val FONT_HEIGHT = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT

class GuiGuideBook(target: Pair<BookEntry, Int> = book.entries.first() to 0) : GuiCommon() {
    val entry = target.first
    val pageNum = target.second
    val hasNextPair = entry.hasNextPair(pageNum)
    val hasPrevPair = pageNum >= 2

    val pages: Pair<Gui, Gui?>
    override val size = Vec2d(286, 186)

    init {
        pages = entry.getPagePair(pageNum).run {
            first.toGuiComponent(this@GuiGuideBook, Page.LEFT) to
                second?.toGuiComponent(this@GuiGuideBook, Page.RIGHT)
        }
    }

    override fun initGui() {
        super.initGui()
        pages.first.initGui()
        pages.second?.initGui()
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        drawBook()

        val worldTime = mc.theWorld?.totalWorldTime?.toDouble() ?: 0.0
        val totalTime = worldTime + partialTicks

        val mouse = Vec2d(mouseX, mouseY)

        pages.first.draw(mouse, totalTime)
        pages.second?.draw(mouse, totalTime)

        if (hasPrevPair) {
            Page.LEFT.drawArrow(mouse - start, this)
        }

        if (hasNextPair) {
            Page.RIGHT.drawArrow(mouse - start, this)
        }

        pages.first.postDraw(mouse, totalTime)
        pages.second?.postDraw(mouse, totalTime)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val mouse = Vec2d(mouseX, mouseY)
        val mouseRelative = mouse - start
        if (mouseButton != 0) {
            return;
        }

        if (hasPrevPair && Page.LEFT.isInsideArrow(mouseRelative)) {
            mc.displayGuiScreen(GuiGuideBook(entry to (pageNum - 2)))
            return
        }

        if (hasNextPair && Page.RIGHT.isInsideArrow(mouseRelative)) {
            mc.displayGuiScreen(GuiGuideBook(entry to (pageNum + 2)))
            return
        }

        if (mouseRelative in Page.LEFT.start to Page.LEFT.end) {
            pages.first.onLeftClick(mouse)
            return
        }
        if (mouseRelative in Page.RIGHT.start to Page.RIGHT.end) {
            pages.second?.onLeftClick(mouse)
            return
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    fun drawBook() {
        GlStateManager.pushMatrix()
        mc.textureManager.bindTexture(BOOK)
        GlStateManager.color(1f, 1f, 1f, 1f)
        drawModalRectWithCustomSizedTexture(start.xi, start.yi, 0f, 0f, size.xi, size.yi, 512f, 512f)
        GlStateManager.popMatrix()
    }

    fun drawString(pos: Vec2d, text: String) {
        fontRenderer.drawString(text, pos.xi, pos.yi, -16777216)
    }

    fun getStringWidth(text: String) = fontRenderer.getStringWidth(text)

    fun getCharWidth(char: Char) = fontRenderer.getCharWidth(char)

    enum class Page(
        val start: Vec2d,
        val end: Vec2d,
        val arrowUV: Vec2d,
        val arrowUVHover: Vec2d,
        val isInsideArrow: Vec2d.() -> Boolean,
        val arrowPos: Vec2d
    ) {
        LEFT(
            start = Vec2d(19, 19),
            end = Vec2d(127, 160),
            arrowUV = Vec2d(4, 188),
            arrowUVHover = Vec2d(4, 218),
            isInsideArrow = { this in (LEFT.arrowPos to (LEFT.arrowPos + ARROW_SIZE))},
            arrowPos = Vec2d(30, 164)
        ),
        RIGHT(
            start = Vec2d(153, 19),
            end = Vec2d(251, 160),
            arrowUV = Vec2d(26, 188),
            arrowUVHover = Vec2d(26, 218),
            isInsideArrow = { this in (RIGHT.arrowPos to ( RIGHT.arrowPos + ARROW_SIZE))},
            arrowPos = Vec2d(232, 164)
        );

        fun drawArrow(mouse: Vec2d, parent: GuiGuideBook) {
            val arrowStart = parent.start + arrowPos

            GlStateManager.pushMatrix()
            parent.mc.textureManager.bindTexture(BOOK)
            GlStateManager.color(1f, 1f, 1f, 1f)

            if (mouse.isInsideArrow()) {
                drawModalRectWithCustomSizedTexture(
                    arrowStart.xi, arrowStart.yi,
                    arrowUVHover.x.toFloat(), arrowUVHover.y.toFloat(),
                    ARROW_SIZE.xi, ARROW_SIZE.yi,
                    512f, 512f
                )
            } else {
                drawModalRectWithCustomSizedTexture(
                    arrowStart.xi, arrowStart.yi,
                    arrowUV.x.toFloat(), arrowUV.y.toFloat(),
                    ARROW_SIZE.xi, ARROW_SIZE.yi,
                    512f, 512f
                )
            }

            GlStateManager.popMatrix()
        }
    }
}
