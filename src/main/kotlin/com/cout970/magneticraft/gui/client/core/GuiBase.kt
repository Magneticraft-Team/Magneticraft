package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Keyboard
import org.lwjgl.input.Mouse
import java.io.IOException


/**
 * Created by cout970 on 20/05/2016.
 */
abstract class GuiBase(override val container: ContainerBase) : GuiContainer(container), IGui {

    override val components = mutableListOf<IComponent>()

    override val pos: IVector2 get() = Vec2d(guiLeft, guiTop)
    override val size: IVector2 get() = Vec2d(xSize, ySize)

    override val fontHelper: FontRenderer get() = super.fontRenderer

    override fun initGui() {
        super.initGui()
        components.clear()
        initComponents()
        components.forEach { it.gui = this }
        components.forEach { it.init() }
        super.initGui()
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun IComponent.unaryPlus() {
        components.add(this)
    }

    abstract fun initComponents()

    override fun getWindowSize(): Vec2d = Vec2d(width, height)

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.drawDefaultBackground()
        super.drawScreen(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        components.forEach { it.drawFirstLayer(Vec2d(mouseX, mouseY), partialTicks) }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        components.forEach { it.drawSecondLayer(Vec2d(mouseX, mouseY)) }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        val block = components.any { it ->
            it.onMouseClick(Vec2d(mouseX, mouseY), mouseButton)
        }
        if (!block) {
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        val block = components.any { it ->
            it.onMouseClickMove(Vec2d(mouseX, mouseY), clickedMouseButton, timeSinceLastClick)
        }
        if (!block) {
            super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick)
        }
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        components.forEach { it.onMouseReleased(Vec2d(mouseX, mouseY), state) }
        super.mouseReleased(mouseX, mouseY, state)
    }

    @Throws(IOException::class)
    override fun keyTyped(typedChar: Char, keyCode: Int) {
        val block = components.any { it -> it.onKeyTyped(typedChar, keyCode) }
        if (!block) {
            super.keyTyped(typedChar, keyCode)
        }
    }

    @Throws(IOException::class)
    override fun handleKeyboardInput() {
        val char = Keyboard.getEventCharacter()
        val event = Keyboard.getEventKey()
        val state = Keyboard.getEventKeyState()

        if ((event == 0 && char >= ' ') || state) {
            this.keyTyped(char, event)
        }

        if (!state) {
            components.any { it -> it.onKeyReleased(char, event) }
        }

        this.mc.dispatchKeypresses()
    }

    override fun handleMouseInput() {
        super.handleMouseInput()
        val dw = Mouse.getEventDWheel()
        if (dw != 0) {
            components.forEach { it.onWheel(dw / Math.abs(dw)) }
        }
    }

    override fun onGuiClosed() {
        components.forEach { it.onGuiClosed() }
        super.onGuiClosed()
    }

    var sizeX: Int
        get() = super.xSize
        set(value) {
            super.xSize = value
        }

    var sizeY: Int
        get() = super.ySize
        set(value) {
            super.ySize = value
        }

    //render utilities

    override fun bindTexture(res: ResourceLocation) {
        Minecraft.getMinecraft().renderEngine.bindTexture(res)
    }

    override fun drawHoveringText(textLines: List<String>, pos: Vec2d) {
        super.drawHoveringText(textLines, pos.xi - this.pos.xi, pos.yi - this.pos.yi)
    }

    override fun drawCenteredString(text: String, pos: Vec2d, color: Int) {
        drawCenteredString(fontRenderer, text, pos.xi, pos.yi, color)
    }

    override fun drawString(text: String, pos: Vec2d, color: Int) {
        drawString(fontRenderer, text, pos.xi, pos.yi, color)
    }

    override fun drawShadelessString(text: String, pos: Vec2d, color: Int) {
        fontRenderer.drawString(text, pos.xi.toFloat(), pos.yi.toFloat(), color, false)
    }

    override fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int) {
        super.drawHorizontalLine(startX, endX, y, color)
    }

    override fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int) {
        super.drawVerticalLine(x, startY, endY, color)
    }

    override fun drawColor(start: IVector2, end: IVector2, color: Int) {
        drawRect(start.xi, start.yi, end.xi, end.yi, color)
    }

    override fun drawColorGradient(start: IVector2, end: IVector2, startColor: Int, endColor: Int) {
        drawGradientRect(start.xi, start.yi, end.xi, end.yi, startColor, endColor)
    }

    override fun drawSprite(pos: IVector2, size: IVector2, sprite: Sprite) {
        drawTexturedModalRect(pos.xi, pos.yi, sprite, size.xi, size.yi)
    }

    override fun drawTexture(box: DrawableBox) {
        drawScaledCustomSizeModalRect(
                box.screenPos.xi, box.screenPos.yi,
                box.texturePos.xf, box.texturePos.yf,
                box.textureSize.xi, box.textureSize.yi,
                box.screenSize.xi, box.screenSize.yi,
                box.textureScale.xf, box.textureScale.yf
        )
    }

    override fun drawStack(stack: ItemStack, pos: Vec2d, text: String?) {
        RenderHelper.enableGUIStandardItemLighting()

        itemRender.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi)
        itemRender.renderItemOverlayIntoGUI(fontRenderer, stack, pos.xi, pos.yi, text)

        RenderHelper.disableStandardItemLighting()
    }
}