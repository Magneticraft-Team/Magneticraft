package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.RenderItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

open class GuiCommon : GuiScreen() {
    val itemRenderer: RenderItem?
        get() = super.itemRender

    val fontRenderer: FontRenderer
        get() = super.fontRendererObj

    open val size = Vec2d(176, 166)
    var start = Vec2d(0, 0)

    override fun initGui() {
        start = Vec2d((width - size.x) / 2, (height - size.y) / 2)
    }

    fun renderToolTip(stack: ItemStack, pos: Vec2d) {
        super.renderToolTip(stack, pos.xi, pos.yi)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
//        mc.textureManager.bindTexture(BACKGROUND)
//        drawTexturedModalRect(start.xi, start.yi, 0, 0, size.xi, size.yi)
        GlStateManager.color(1f, 1f, 1f, 1f)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun doesGuiPauseGame() = false

    fun drawTexture(position: Vec2d, size: Vec2d, texture: ResourceLocation) {
        GlStateManager.pushMatrix()
        GlStateManager.color(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA)

        mc.renderEngine.bindTexture(texture)
        drawScaledCustomSizeModalRect(position.xi, position.yi, 0f, 0f, 256, 256, size.xi, size.yi, 256f, 256f)

        GlStateManager.popMatrix()
    }

    fun drawHoveringText(text: List<String>, pos: Vec2d) {
        drawHoveringText(text, pos.xi, pos.yi, fontRenderer)
    }

    fun drawStack(stack: ItemStack, pos: Vec2d, text: String? = null) {
        GlStateManager.pushMatrix()
        RenderHelper.enableGUIStandardItemLighting()

        itemRenderer?.renderItemAndEffectIntoGUI(stack, pos.xi, pos.yi)
        itemRenderer?.renderItemOverlayIntoGUI(fontRenderer, stack, pos.xi, pos.yi, text)

        RenderHelper.disableStandardItemLighting()
        GlStateManager.popMatrix()
    }
}