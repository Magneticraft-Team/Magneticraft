package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.util.vector.*
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 20/05/2016.
 */
interface IGuiRenderer {

    //bind texture
    fun bindTexture(res: ResourceLocation)

    //string render
    fun drawHoveringText(textLines: List<String>, pos: Vec2d)

    fun drawCenteredString(text: String, pos: Vec2d, color: Int)

    fun drawString(text: String, pos: Vec2d, color: Int)

    fun drawShadelessString(text: String, pos: Vec2d, color: Int)

    //line render
    fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int)

    fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int)

    //box render
    fun drawColor(start: Vec2d, end: Vec2d, color: Int)

    fun drawColor(rect: Pair<Vec2d, Vec2d>, color: Int) = drawColor(rect.start, rect.end, color)

    fun drawColorGradient(start: Vec2d, end: Vec2d, startColor: Int, endColor: Int)

    fun drawColorGradient(rect: Pair<Vec2d, Vec2d>, startColor: Int, endColor: Int) {
        drawColorGradient(rect.start, rect.end, startColor, endColor)
    }

    fun drawSprite(rect: Pair<Vec2d, Vec2d>, sprite: Sprite) = drawSprite(rect.pos, rect.size, sprite)
    fun drawSprite(start: Vec2d, end: Vec2d, sprite: Sprite)

    //texture render
    fun drawTexture(box: DrawableBox)

    //itemstack render
    fun drawStack(stack: ItemStack, pos: Vec2d, text: String? = null)
}