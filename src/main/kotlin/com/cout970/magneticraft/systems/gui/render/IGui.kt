package com.cout970.magneticraft.systems.gui.render

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.systems.gui.ContainerBase
import net.minecraft.client.gui.FontRenderer

/**
 * Created by cout970 on 20/05/2016.
 */

interface IGui : IGuiRenderer {

    val container: ContainerBase
    val components: MutableList<IComponent>

    val pos: IVector2
    val size: IVector2
    val windowSize: IVector2

    val fontHelper: FontRenderer
}

