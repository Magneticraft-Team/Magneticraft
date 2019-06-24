package com.cout970.magneticraft.systems.gui.json

import com.cout970.magneticraft.api.energy.IElectricNode

abstract class AbstractGuiComponent {
    abstract fun render()
}

class GuiComponentElectricNode(val node: IElectricNode) : AbstractGuiComponent() {

    override fun render() {

    }
}