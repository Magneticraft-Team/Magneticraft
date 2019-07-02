package com.cout970.magneticraft.systems.gui.render

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.Sprite
import com.cout970.magneticraft.misc.vector.vec2Of
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation

/**
 * Created by cout970 on 20/05/2016.
 */
interface IGuiRenderer {

    //bind texture
    fun bindTexture(res: ResourceLocation)

    //string render
    fun drawHoveringText(textLines: List<String>, pos: IVector2)

    fun drawCenteredString(text: String, pos: IVector2, color: Int)

    fun drawString(text: String, pos: IVector2, color: Int)

    fun drawShadelessString(text: String, pos: IVector2, color: Int)

    //line render
    fun drawHorizontalLine(startX: Int, endX: Int, y: Int, color: Int)

    fun drawVerticalLine(x: Int, startY: Int, endY: Int, color: Int)

    //box render
    fun drawColor(start: IVector2, end: IVector2, color: Int)

    fun drawColorGradient(start: IVector2, end: IVector2, startColor: Int, endColor: Int)

    fun drawSprite(pos: IVector2, size: IVector2, sprite: Sprite)

    //texture render
    fun drawTexture(box: DrawableBox)

    fun drawTexture(screenPos: IVector2, screenSize: IVector2,
                    texturePos: IVector2, textureSize: IVector2 = screenSize,
                    textureScale: IVector2 = vec2Of(256))

    //itemstack render
    fun drawStack(stack: ItemStack, pos: IVector2, text: String? = null)
}