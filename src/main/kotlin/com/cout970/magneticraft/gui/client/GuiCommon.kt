package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.Coords
import com.cout970.magneticraft.util.resource
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.client.renderer.RenderItem
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

val BACKGROUND = resource("textures/gui/background.png")

open class GuiCommon : GuiScreen() {
    val itemRenderer: RenderItem?
        get() = super.itemRender

    val fontRenderer: FontRenderer
        get() = super.fontRendererObj

    open val size = Coords(176, 166)
    var start = Coords(0, 0)

    override fun initGui() {
        start = Coords((width - size.x) / 2, (height - size.y) / 2)
    }

    fun renderToolTip(stack: ItemStack, pos: Coords) {
        super.renderToolTip(stack, pos.x, pos.y)
    }

    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        mc.textureManager.bindTexture(BACKGROUND)
        drawTexturedModalRect(start.x, start.y, 0, 0, size.x, size.y)
        GlStateManager.color(1f, 1f, 1f, 1f)
        super.drawScreen(mouseX, mouseY, partialTicks)
    }

    override fun doesGuiPauseGame() = false

    fun drawTexture(position: Coords, size: Coords, texture: ResourceLocation) {
        mc.renderEngine.bindTexture(texture)
        drawScaledCustomSizeModalRect(position.x, position.y, 0f, 0f, 256, 256, size.x, size.y, 256f, 256f)
    }

    fun drawHoveringText(text: List<String>, pos: Coords) {
        drawHoveringText(text, pos.x, pos.y, fontRenderer)
    }

    fun drawStack(stack: ItemStack, pos: Coords, text: String? = null) {
        GlStateManager.pushMatrix()
        RenderHelper.enableGUIStandardItemLighting()

        itemRenderer?.renderItemAndEffectIntoGUI(stack, pos.x, pos.y)
        itemRenderer?.renderItemOverlayIntoGUI(fontRenderer, stack, pos.x, pos.y, text)

        RenderHelper.disableStandardItemLighting()
        GlStateManager.popMatrix()
    }
}