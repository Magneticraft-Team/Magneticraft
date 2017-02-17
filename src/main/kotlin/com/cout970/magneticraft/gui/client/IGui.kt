package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 20/05/2016.
 */

interface IGui : IGuiRenderer {

    val components: MutableList<IComponent>

    val box: Box

    val container: ContainerBase

    fun getWindowSize(): Vec2d

}

