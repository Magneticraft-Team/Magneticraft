package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.client.renderer.texture.TextureAtlasSprite
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

    //line render
    fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int)

    fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int)

    //box render
    fun drawBox(start: Vec2d, end: Vec2d, color: Int) = drawBox(Box(start, end - start), color)

    fun drawBox(box: Box, color: Int)

    fun drawGradientBox(start: Vec2d, end: Vec2d, startColor: Int, endColor: Int) =
            drawGradientBox(Box(start, end - start), startColor, endColor)

    fun drawGradientBox(box: Box, startColor: Int, endColor: Int)

    //texture render
    fun drawTexture(pos: Vec2d, size: Vec2d, textureOffset: Vec2d) =
            drawTexture(Box(pos, size), textureOffset)

    fun drawTexture(box: Box, textureOffset: Vec2d)

    fun drawTexture(pos: Vec2d, size: Vec2d, textureSprite: TextureAtlasSprite) =
            drawTexture(Box(pos, size), textureSprite)

    fun drawTexture(box: Box, textureSprite: TextureAtlasSprite)

    fun drawScaledTexture(pos: Vec2d, size: Vec2d, uv: Vec2d, textureSize: Vec2d) =
            drawScaledTexture(Box(pos, size), uv, textureSize)

    fun drawScaledTexture(box: Box, uv: Vec2d, textureSize: Vec2d)

    fun drawScaledTexture(pos: Vec2d, size: Vec2d, uvMin: Vec2d, uvMax: Vec2d, textureSize: Vec2d) =
            drawScaledTexture(Box(pos, size), uvMin, uvMax, textureSize)

    fun drawScaledTexture(box: Box, uvMin: Vec2d, uvMax: Vec2d, textureSize: Vec2d)

    //itemstack render
    fun drawStack(stack: ItemStack, pos: Vec2d, text: String? = null)
}