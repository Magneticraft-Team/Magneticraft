package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import java.io.IOException

/**
 * Created by cout970 on 20/05/2016.
 */
abstract class GuiBase(override val container: ContainerBase) : GuiContainer(container), IGui {

    override val components = mutableListOf<IComponent>()

    override val box: Box get() = Box(Vec2d(guiLeft, guiTop), Vec2d(xSize, ySize))

    override fun initGui() {
        super.initGui()
        components.clear()
        initComponents()
        components.forEach { it.gui = this }
    }

    abstract fun initComponents()

    override fun getWindowSize(): Vec2d = Vec2d(width, height)

    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
        components.forEach { it.drawFirstLayer(Vec2d(mouseX, mouseY), partialTicks) }
    }

    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        components.forEach { it.drawSecondLayer(Vec2d(mouseX, mouseY)) }
    }

    @Throws(IOException::class)
    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        var block = false
        for (it in components) {
            if (it.onMouseClick(Vec2d(mouseX, mouseY), mouseButton)) {
                block = true
                break
            }
        }
        if (!block) {
            super.mouseClicked(mouseX, mouseY, mouseButton)
        }
    }

    override fun mouseClickMove(mouseX: Int, mouseY: Int, clickedMouseButton: Int, timeSinceLastClick: Long) {
        var block = false
        for (it in components) {
            if (it.onMouseClickMove(Vec2d(mouseX, mouseY), clickedMouseButton, timeSinceLastClick)) {
                block = true
                break
            }
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
        var block = false
        for (it in components) {
            if (it.onKeyTyped(typedChar, keyCode)) {
                block = true
                break
            }
        }
        if (!block) {
            super.keyTyped(typedChar, keyCode)
        }
    }

    override fun onGuiClosed() {
        components.forEach { it.onGuiClosed() }
        super.onGuiClosed()
    }

    //render utilities

    override fun bindTexture(res: ResourceLocation) {
        Minecraft.getMinecraft().renderEngine.bindTexture(res)
    }

    override fun drawHoveringText(textLines: List<String>, pos: Vec2d) {
        super.drawHoveringText(textLines, pos.xi - box.pos.xi, pos.yi - box.pos.yi)
    }

    override fun drawCenteredString(text: String, pos: Vec2d, color: Int) {
        drawCenteredString(fontRendererObj, text, pos.xi, pos.yi, color)
    }

    override fun drawString(text: String, pos: Vec2d, color: Int) {
        drawString(fontRendererObj, text, pos.xi, pos.yi, color)
    }

    override fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int) {
        super.drawHorizontalLine(startX, endX, y, color)
    }

    override fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int) {
        super.drawVerticalLine(x, startY, endY, color)
    }

    override fun drawBox(box: Box, color: Int) {
        drawRect(box.start.xi, box.start.yi, box.end.xi, box.end.yi, color)
    }

    override fun drawGradientBox(box: Box, startColor: Int, endColor: Int) {
        drawGradientRect(box.start.xi, box.start.yi, box.end.xi, box.end.yi, startColor, endColor)
    }

    override fun drawTexture(box: Box, textureOffset: Vec2d) {
        drawTexturedModalRect(box.pos.xi, box.pos.yi, textureOffset.xi, textureOffset.yi, box.size.xi, box.size.yi)
    }

    override fun drawTexture(box: Box, textureSprite: TextureAtlasSprite) {
        drawTexturedModalRect(box.pos.xi, box.pos.yi, textureSprite, box.size.xi, box.size.yi)
    }

    override fun drawScaledTexture(box: Box, uv: Vec2d, textureSize: Vec2d) {
        drawModalRectWithCustomSizedTexture(box.pos.xi, box.pos.yi, uv.xf, uv.yf, box.size.xi,
                box.size.yi, textureSize.xf, textureSize.yf)
    }

    override fun drawScaledTexture(box: Box, uvMin: Vec2d, uvMax: Vec2d, textureSize: Vec2d) {
        drawScaledCustomSizeModalRect(box.pos.xi, box.pos.yi, uvMin.xf, uvMin.yf, uvMax.xi,
                uvMax.yi, box.size.xi, box.size.yi, textureSize.xf, textureSize.yf)
    }

    override fun drawStack(stack: ItemStack, pos: Vec2d, text: String?) {
        RenderHelper.enableGUIStandardItemLighting()

        itemRender.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi)
        itemRender.renderItemOverlayIntoGUI(fontRendererObj, stack, pos.xi, pos.yi, text)

        RenderHelper.disableStandardItemLighting()
    }
}