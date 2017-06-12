package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase

/**
 * Created by cout970 on 2017/06/12.
 */

class GuiTileBox(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground("box"))
    }
}

