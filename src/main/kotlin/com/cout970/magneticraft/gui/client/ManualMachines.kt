package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.util.guiTexture

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiBox(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground(guiTexture("box")))
    }
}
