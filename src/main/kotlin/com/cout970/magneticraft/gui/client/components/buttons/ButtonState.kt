package com.cout970.magneticraft.gui.client.components.buttons

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