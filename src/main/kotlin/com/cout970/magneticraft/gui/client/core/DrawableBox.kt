package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.util.vector.pos
import com.cout970.magneticraft.util.vector.size

/**
 * Created by cout970 on 2017/08/01.
 */
data class DrawableBox(
        val screen: Pair<IVector2, IVector2>,
        val texture: Pair<IVector2, IVector2>,
        val textureSize: IVector2
) {
    fun offset(pos: IVector2): DrawableBox = copy(screen = screen.pos + pos to screen.size)
}