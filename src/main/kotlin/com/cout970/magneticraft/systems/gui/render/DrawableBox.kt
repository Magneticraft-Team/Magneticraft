package com.cout970.magneticraft.systems.gui.render

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.vec2Of
import net.minecraft.client.gui.Gui

/**
 * Created by cout970 on 2017/08/01.
 */
data class DrawableBox(
    val screenPos: IVector2,
    val screenSize: IVector2,
    val texturePos: IVector2,
    val textureSize: IVector2 = screenSize,
    val textureScale: IVector2 = vec2Of(256)
) {
    fun offset(pos: IVector2): DrawableBox = copy(screenPos = screenPos + pos)

    fun draw() {
        Gui.drawScaledCustomSizeModalRect(
            screenPos.xi, screenPos.yi,
            texturePos.xf, texturePos.yf,
            textureSize.xi, textureSize.yi,
            screenSize.xi, screenSize.yi,
            textureScale.xf, textureScale.yf
        )
    }
}