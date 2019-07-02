package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.gui.SlotType
import com.cout970.magneticraft.misc.gui.TypedSlot
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.GuiBase
import net.minecraft.inventory.Slot

class JsonGui(override val container: JsonContainer) : GuiBase(container) {

    lateinit var background: List<DrawableBox>
    lateinit var slots: List<DrawableBox>

    override fun initGui() {
        val config = container.gui.config
        this.xSize = config.background.xi
        this.ySize = config.background.yi
        super.initGui()

        this.slots = createSlots(container.inventorySlots)
        this.background = if (config.top) {
            createRectWithBorders(pos, size)
        } else {
            createRectWithBorders(pos + vec2Of(0, 76), size - vec2Of(0, 76))
        }
    }

    override fun initComponents() {
        container.gui.build(this)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        bindTexture(guiTexture("misc"))
        background.forEach { it.draw() }
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY)
        slots.forEach { it.draw() }
    }

    fun createRectWithBorders(pPos: IVector2, pSize: IVector2): List<DrawableBox> {
        val leftUp = DrawableBox(
            screenPos = pPos,
            screenSize = vec2Of(4),
            texturePos = vec2Of(0),
            textureSize = vec2Of(4)
        )

        val leftDown = DrawableBox(
            screenPos = pPos + vec2Of(0, pSize.yi - 4),
            screenSize = vec2Of(4),
            texturePos = vec2Of(0, 5),
            textureSize = vec2Of(4)
        )

        val rightUp = DrawableBox(
            screenPos = pPos + vec2Of(pSize.xi - 4, 0),
            screenSize = vec2Of(4),
            texturePos = vec2Of(5, 0),
            textureSize = vec2Of(4)
        )

        val rightDown = DrawableBox(
            screenPos = pPos + vec2Of(pSize.xi - 4, pSize.yi - 4),
            screenSize = vec2Of(4),
            texturePos = vec2Of(5, 5),
            textureSize = vec2Of(4)
        )

        val left = DrawableBox(
            screenPos = pPos + vec2Of(0, 4),
            screenSize = vec2Of(4, pSize.y - 8),
            texturePos = vec2Of(0, 10),
            textureSize = vec2Of(4, pSize.y - 8)
        )

        val right = DrawableBox(
            screenPos = pPos + vec2Of(pSize.xi - 4, 4),
            screenSize = vec2Of(4, pSize.yi - 8),
            texturePos = vec2Of(5, 10),
            textureSize = vec2Of(4, pSize.yi - 8)
        )

        val up = DrawableBox(
            screenPos = pPos + vec2Of(4, 0),
            screenSize = vec2Of(pSize.xi - 8, 4),
            texturePos = vec2Of(10, 0),
            textureSize = vec2Of(pSize.xi - 8, 4)
        )

        val down = DrawableBox(
            screenPos = pPos + vec2Of(4, pSize.yi - 4),
            screenSize = vec2Of(pSize.xi - 8, 4),
            texturePos = vec2Of(10, 5),
            textureSize = vec2Of(pSize.xi - 8, 4)
        )

        val center = DrawableBox(
            screenPos = pPos + vec2Of(4, 4),
            screenSize = vec2Of(pSize.xi - 8, pSize.yi - 8),
            texturePos = vec2Of(5, 3),
            textureSize = vec2Of(1, 1)
        )

        return listOf(
            leftUp, leftDown, rightUp, rightDown,
            left, right, up, down, center
        )
    }

    fun createSlots(slots: List<Slot>): List<DrawableBox> {
        val boxes = mutableListOf<DrawableBox>()

        slots.forEach { slot ->
            val x = guiLeft + slot.xPos - 1
            val y = guiTop + slot.yPos - 1

            val type = (slot as? TypedSlot)?.type ?: SlotType.NORMAL
            val icon = when (type) {
                SlotType.INPUT -> vec2Of(55, 81)
                SlotType.OUTPUT -> vec2Of(36, 81)
                SlotType.FILTER -> vec2Of(74, 81)
                SlotType.NORMAL -> vec2Of(36, 100)
            }

            boxes += DrawableBox(
                screenPos = vec2Of(x, y),
                screenSize = vec2Of(18),
                texturePos = icon,
                textureSize = vec2Of(18)
            )
        }

        return boxes
    }
}