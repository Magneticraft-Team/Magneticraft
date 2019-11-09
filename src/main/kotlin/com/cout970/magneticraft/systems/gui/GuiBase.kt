package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.misc.render.GL
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.gui.AbstractGui
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.screen.inventory.ContainerScreen
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraft.util.text.StringTextComponent
import net.minecraftforge.fml.client.config.GuiUtils
import java.io.IOException


/**
 * Created by cout970 on 20/05/2016.
 */
abstract class GuiBase<T : ContainerBase>(override val containerBase: T) : ContainerScreen<T>(
    containerBase,
    containerBase.player.inventory,
    StringTextComponent("")
), IGui {

    override val components = mutableListOf<IComponent>()

    override val pos: IVector2 get() = vec2Of(guiLeft, guiTop)
    override val size: IVector2 get() = vec2Of(xSize, ySize)
    override val windowSize: IVector2 get() = vec2Of(width, height)

    var keyHandled = false

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

    override val fontHelper: FontRenderer get() = super.font

    override fun init() {
        super.init()
        components.clear()
        initComponents()
        components.forEach { it.gui = this }
        super.init()
        components.forEach { it.init() }
    }

    @Suppress("NOTHING_TO_INLINE")
    inline operator fun IComponent.unaryPlus() {
        components.add(this)
    }

    abstract fun initComponents()

    override fun render(mouseX: Int, mouseY: Int, partialTicks: Float) {
        this.renderBackground()
        super.render(mouseX, mouseY, partialTicks)
        this.renderHoveredToolTip(mouseX, mouseY)
    }

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        GL.color(1f, 1f, 1f, 1f)
        components.forEach { it.drawFirstLayer(Vec2d(mouseX, mouseY), partialTicks) }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        components.forEach { it.drawSecondLayer(Vec2d(mouseX, mouseY)) }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Double, mouseY: Double, mouseButton: Int): Boolean {
        val block = components.any {
            it.onMouseClick(Vec2d(mouseX, mouseY), mouseButton)
        }
        if (!block) {
            return super.mouseClicked(mouseX, mouseY, mouseButton)
        }
        return block
    }

    override fun mouseMoved(mouseX: Double, mouseY: Double) {
        val block = components.any {
            it.onMouseClickMove(Vec2d(mouseX, mouseY))
        }
        if (!block) {
            super.mouseMoved(mouseX, mouseY)
        }
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, state: Int): Boolean {
        components.forEach { it.onMouseReleased(Vec2d(mouseX, mouseY), state) }
        return super.mouseReleased(mouseX, mouseY, state)
    }

    override fun charTyped(p_charTyped_1_: Char, p_charTyped_2_: Int): Boolean {
        return super.charTyped(p_charTyped_1_, p_charTyped_2_)
    }

    override fun keyPressed(typedChar: Int, keyCode: Int, unknown: Int): Boolean {
        this.keyHandled = components.any { it.onKeyTyped(typedChar.toChar(), keyCode) }
        if (!this.keyHandled) {
            super.keyPressed(typedChar, keyCode, unknown)
        }
        return this.keyHandled
    }

//    @Throws(IOException::class)
//    override fun keyTyped(typedChar: Char, keyCode: Int) {
//        this.keyHandled = components.any { it.onKeyTyped(typedChar, keyCode) }
//        if (!this.keyHandled) {
//            super.keyTyped(typedChar, keyCode)
//        }
//    }

//    @Throws(IOException::class)
//    override fun handleKeyboardInput() {
//        val char = Keyboard.getEventCharacter()
//        val event = Keyboard.getEventKey()
//        val state = Keyboard.getEventKeyState()
//
//        if (Debug.DEBUG && char.toInt() == 0 && event == 64) {
//            this.minecraft?.textureManager?.deleteTexture(guiTexture("misc"))
//        }
//
//        if ((event == 0 && char >= ' ') || state) {
//            this.keyTyped(char, event)
//        }
//
//        if (!state) {
//            components.any { it.onKeyReleased(char, event) }
//        }
//
//        this.mc.dispatchKeypresses()
//    }

//    override fun handleMouseInput() {
//        super.handleMouseInput()
//        val dw = Mouse.getEventDWheel()
//        if (dw != 0) {
//            components.forEach { it.onWheel(dw / Math.abs(dw)) }
//        }
//    }

    override fun onClose() {
        components.forEach { it.onGuiClosed() }
        super.onClose()
    }

    //render utilities

    override fun bindTexture(res: ResourceLocation) {
        minecraft?.textureManager?.bindTexture(res)
    }

    override fun drawHoveringText(textLines: List<String>, pos: Vec2d) {
        GuiUtils.drawHoveringText(textLines, pos.xi - this.pos.xi, pos.yi - this.pos.yi, width, height, 9999999, font)
    }

    override fun drawCenteredString(text: String, pos: Vec2d, color: Int) {
        drawCenteredString(font, text, pos.xi, pos.yi, color)
    }

    override fun drawString(text: String, pos: Vec2d, color: Int) {
        drawString(font, text, pos.xi, pos.yi, color)
    }

    override fun drawShadelessString(text: String, pos: Vec2d, color: Int) {
        font.drawString(text, pos.xi.toFloat(), pos.yi.toFloat(), color)
    }

    override fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int) {
        hLine(startX, endX, y, color)
    }

    override fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int) {
        vLine(x, startY, endY, color)
    }

    override fun drawColor(start: IVector2, end: IVector2, color: Int) {
        GuiUtils.drawGradientRect(0, start.xi, start.yi, end.xi, end.yi, color, color)
    }

    override fun drawColorGradient(start: IVector2, end: IVector2, startColor: Int, endColor: Int) {
        GuiUtils.drawGradientRect(0, start.xi, start.yi, end.xi, end.yi, startColor, endColor)
    }

    override fun drawSprite(pos: IVector2, size: IVector2, sprite: Sprite) {
        AbstractGui.blit(
            pos.xi, pos.yi,
            sprite.width, sprite.height,
            sprite.minU, sprite.minV,
            size.xi, size.yi,
            256, 256
        )
    }

    override fun drawTexture(box: DrawableBox) {
        AbstractGui.blit(
            box.screenPos.xi, box.screenPos.yi,
            box.textureSize.xi, box.textureSize.yi,
            box.texturePos.xf, box.texturePos.yf,
            box.screenSize.xi, box.screenSize.yi,
            box.textureScale.xi, box.textureScale.yi
        )
    }

    override fun drawTexture(screenPos: IVector2, screenSize: IVector2,
                             texturePos: IVector2, textureSize: IVector2,
                             textureScale: IVector2) {

        AbstractGui.blit(
            screenPos.xi, screenPos.yi,
            textureSize.xi, textureSize.yi,
            texturePos.xf, texturePos.yf,
            screenSize.xi, screenSize.yi,
            textureScale.xi, textureScale.yi
        )
    }

    override fun drawTexture(
        screenPosX: Int, screenPosY: Int,
        screenSizeX: Int, screenSizeY: Int,
        texturePosX: Float, texturePosY: Float,
        textureSizeX: Int, textureSizeY: Int,
        textureScaleX: Int, textureScaleY: Int
    ) {

        AbstractGui.blit(
            screenPosX, screenPosY,
            textureSizeX, textureSizeY,
            texturePosX, texturePosY,
            screenSizeX, screenSizeY,
            textureScaleX, textureScaleY
        )
    }

    override fun drawStack(stack: ItemStack, pos: Vec2d, text: String?) {
        RenderHelper.enableGUIStandardItemLighting()

        itemRenderer.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi)
        itemRenderer.renderItemOverlayIntoGUI(font, stack, pos.xi, pos.yi, text)

        RenderHelper.disableStandardItemLighting()
    }

    companion object{
        fun drawTexture(
            screenPosX: Int, screenPosY: Int,
            screenSizeX: Int, screenSizeY: Int,
            texturePosX: Float, texturePosY: Float,
            textureSizeX: Int, textureSizeY: Int,
            textureScaleX: Int, textureScaleY: Int
        ) {

            AbstractGui.blit(
                screenPosX, screenPosY,
                textureSizeX, textureSizeY,
                texturePosX, texturePosY,
                screenSizeX, screenSizeY,
                textureScaleX, textureScaleY
            )
        }
    }
}