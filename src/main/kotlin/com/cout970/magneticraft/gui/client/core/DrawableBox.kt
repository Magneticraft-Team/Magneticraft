package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.util.vector.vec2Of

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
}