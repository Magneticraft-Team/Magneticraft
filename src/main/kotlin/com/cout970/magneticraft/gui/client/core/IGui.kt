package com.cout970.magneticraft.gui.client.core

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 20/05/2016.
 */

interface IGui : IGuiRenderer {

    val components: MutableList<IComponent>

    val pos: IVector2
    val size: IVector2

    val container: ContainerBase

    fun getWindowSize(): Vec2d
}

