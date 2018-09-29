package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import net.minecraft.util.ResourceLocation

open class ToggleButton(
    id: Int,
    texture: ResourceLocation,
    box: Pair<IVector2, IVector2>,
    textureSize: IVector2,
    uvGetter: (ButtonState) -> Pair<IVector2, IVector2>
) : AbstractButton(id, texture, box, textureSize, uvGetter) {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        state = if (mouse in box) when {
            state.getBase() == ButtonState.PRESSED -> ButtonState.HOVER_PRESSED
            else -> ButtonState.HOVER_UNPRESSED
        } else when {
            state.getBase() == ButtonState.PRESSED -> ButtonState.PRESSED
            else -> ButtonState.UNPRESSED
        }
        super.drawFirstLayer(mouse, partialTicks)
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        val press = super.onMouseClick(mouse, mouseButton)
        if (press) {
            state = if (state.getBase() == ButtonState.UNPRESSED) {
                ButtonState.HOVER_PRESSED
            } else {
                ButtonState.HOVER_UNPRESSED
            }
        }
        return press
    }
}