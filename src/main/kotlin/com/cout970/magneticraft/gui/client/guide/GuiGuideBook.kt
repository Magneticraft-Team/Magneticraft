package com.cout970.magneticraft.gui.client.guide

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.gui.client.GuiCommon
import com.cout970.magneticraft.guide.Entry
import com.cout970.magneticraft.guide.Page.Gui
import com.cout970.magneticraft.guide.contentTable
import com.cout970.magneticraft.util.resource
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager

val BOOK = resource("textures/gui/guide/book.png")
val PAGE_SIZE = Coords(108, 141)
val PAGE_CENTER = PAGE_SIZE.center()
val ARROW_SIZE = Coords(18, 26)
val FONT_HEIGHT = Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT

class GuiGuideBook(target: Pair<Entry, Int> = contentTable to 0) : GuiCommon() {
    val entry = target.first
    val pageNum = target.second
    val hasNextPair = entry.hasNextPair(pageNum)
    val hasPrevPair = pageNum >= 2

    val pages: Pair<Gui, Gui?>
    override val size = Coords(286, 186)

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

        val mouse = Coords(mouseX, mouseY)

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
        val mouse = Coords(mouseX, mouseY)
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

        if (mouseRelative.inside(Page.LEFT.start, Page.LEFT.end)) {
            pages.first.onLeftClick(mouse)
            return
        }
        if (mouseRelative.inside(Page.RIGHT.start, Page.RIGHT.end)) {
            pages.second?.onLeftClick(mouse)
            return
        }
        super.mouseClicked(mouseX, mouseY, mouseButton)
    }

    fun drawBook() {
        GlStateManager.pushMatrix()
        mc.textureManager.bindTexture(BOOK)
        GlStateManager.color(1f, 1f, 1f, 1f)
        drawModalRectWithCustomSizedTexture(start.x, start.y, 0f, 0f, size.x, size.y, 512f, 512f)
        GlStateManager.popMatrix()
    }

    fun drawString(pos: Coords, text: String) {
        fontRenderer.drawString(text, pos.x, pos.y, -16777216)
    }

    fun getStringWidth(text: String) = fontRenderer.getStringWidth(text)

    fun getCharWidth(char: Char) = fontRenderer.getCharWidth(char)

    enum class Page(
        val start: Coords,
        val end: Coords,
        val arrowUV: Coords,
        val arrowUVHover: Coords,
        val isInsideArrow: Coords.() -> Boolean,
        val arrowPos: Coords
    ) {
        LEFT(
            start = Coords(19, 19),
            end = Coords(127, 160),
            arrowUV = Coords(4, 188),
            arrowUVHover = Coords(4, 218),
            isInsideArrow = { inside(LEFT.arrowPos, LEFT.arrowPos + ARROW_SIZE) },
            arrowPos = Coords(30, 164)
        ),
        RIGHT(
            start = Coords(153, 19),
            end = Coords(251, 160),
            arrowUV = Coords(26, 188),
            arrowUVHover = Coords(26, 218),
            isInsideArrow = { inside(RIGHT.arrowPos, RIGHT.arrowPos + ARROW_SIZE) },
            arrowPos = Coords(232, 164)
        );

        fun drawArrow(mouse: Coords, parent: GuiGuideBook) {
            val arrowStart = parent.start + arrowPos

            GlStateManager.pushMatrix()
            parent.mc.textureManager.bindTexture(BOOK)
            GlStateManager.color(1f, 1f, 1f, 1f)

            if (mouse.isInsideArrow()) {
                drawModalRectWithCustomSizedTexture(
                    arrowStart.x, arrowStart.y,
                    arrowUVHover.x.toFloat(), arrowUVHover.y.toFloat(),
                    ARROW_SIZE.x, ARROW_SIZE.y,
                    512f, 512f
                )
            } else {
                drawModalRectWithCustomSizedTexture(
                    arrowStart.x, arrowStart.y,
                    arrowUV.x.toFloat(), arrowUV.y.toFloat(),
                    ARROW_SIZE.x, ARROW_SIZE.y,
                    512f, 512f
                )
            }

            GlStateManager.popMatrix()
        }
    }
}
