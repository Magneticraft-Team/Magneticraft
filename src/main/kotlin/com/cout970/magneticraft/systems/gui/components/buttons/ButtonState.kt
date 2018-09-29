package com.cout970.magneticraft.systems.gui.components.buttons

enum class ButtonState(val parent: ButtonState? = null) {
    UNPRESSED,
    PRESSED,
    HOVER_UNPRESSED(UNPRESSED),
    HOVER_PRESSED(PRESSED);

    fun getBase(): ButtonState {
        if (parent == null) return this
        return parent.getBase()
    }
}