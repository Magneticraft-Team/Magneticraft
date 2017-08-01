package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2

/**
 * Created by cout970 on 2017/08/01.
 */
data class DrawableBox(
        val screen: Pair<IVector2, IVector2>,
        val texture: Pair<IVector2, IVector2>,
        val textureSize: IVector2
)