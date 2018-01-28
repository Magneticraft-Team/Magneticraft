package com.cout970.magneticraft.gui.client.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.isMouseButtonDown
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import net.minecraft.util.ResourceLocation

class SimpleButton(
        id: Int,
        texture: ResourceLocation,
        box: Pair<IVector2, IVector2>,
        textureSize: IVector2,
        uvGetter: (ButtonState) -> Pair<IVector2, IVector2>
) : AbstractButton(id, texture, box, textureSize, uvGetter) {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (mouse in box) {
            state = if (isMouseButtonDown(0)) ButtonState.HOVER_PRESSED else ButtonState.HOVER_UNPRESSED
        } else {
            state = ButtonState.UNPRESSED
        }
        super.drawFirstLayer(mouse, partialTicks)
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        val press = super.onMouseClick(mouse, mouseButton)
        if (press) {
            state = ButtonState.HOVER_PRESSED
        }
        return press
    }
}