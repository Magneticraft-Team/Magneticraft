package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer

class JsonGui(val container: JsonContainer) : GuiContainer(container) {

    lateinit var background: List<DrawableBox>
    lateinit var slots: List<DrawableBox>

    override fun initGui() {
        val config = container.config
        this.xSize = config.background.sizeX
        this.ySize = config.background.sizeY
        super.initGui()
        this.background = createRectWithBorders(vec2Of(guiLeft, guiTop), vec2Of(xSize, ySize))

        val slotConfig = if (config.playerInventory) {
            val offsetX = xSize - 176
            val offsetY = ySize - 166
            config.slots + listOf(
                GuiConfig.SlotGroup(0, 3, 9, offsetX + 8, offsetY + 84, "default"),
                GuiConfig.SlotGroup(27, 1, 9, offsetX + 8, offsetY + 142, "default")
            )
        } else {
            config.slots
        }

        this.slots = createSlots(slotConfig)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        Minecraft.getMinecraft().renderEngine.bindTexture(guiTexture("misc"))
        background.forEach { it.draw() }
        slots.forEach { it.draw() }
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
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

    fun createSlots(slots: List<GuiConfig.SlotGroup>): List<DrawableBox> {
        val boxes = mutableListOf<DrawableBox>()

        slots.forEach { group ->
            repeat(group.rows) { row ->
                repeat(group.columns) loop@{ column ->
                    val x = guiLeft + group.posX + column * 18 - 1
                    val y = guiTop + group.posY + row * 18 - 1

                    val icon = when (group.icon) {
                        "in" -> vec2Of(55, 81)
                        "out" -> vec2Of(36, 81)
                        "filter" -> vec2Of(74, 81)
                        else -> vec2Of(36, 100)
                    }

                    boxes += DrawableBox(
                        screenPos = vec2Of(x, y),
                        screenSize = vec2Of(18),
                        texturePos = icon,
                        textureSize = vec2Of(18)
                    )
                }
            }
        }

        return boxes
    }
}