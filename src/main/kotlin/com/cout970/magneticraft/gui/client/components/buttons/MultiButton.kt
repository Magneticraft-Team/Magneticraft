package com.cout970.magneticraft.gui.client.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.pos
import com.cout970.magneticraft.util.vector.size
import net.minecraft.util.ResourceLocation

class MultiButton(
        id: Int,
        texture: ResourceLocation,
        box: Pair<IVector2, IVector2>,
        textureSize: IVector2,
        uvGetter: (ButtonState) -> Pair<IVector2, IVector2>
) : AbstractButton(id, texture, box, textureSize, uvGetter) {

    lateinit var allButtons: List<MultiButton>

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (mouse in (gui.pos + box.pos to box.size)) when {
            state.getBase() == ButtonState.PRESSED -> state = ButtonState.HOVER_PRESSED
            else -> state = ButtonState.HOVER_UNPRESSED
        } else when {
            state.getBase() == ButtonState.PRESSED -> state = ButtonState.PRESSED
            else -> state = ButtonState.UNPRESSED
        }
        super.drawFirstLayer(mouse, partialTicks)
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        val press = super.onMouseClick(mouse, mouseButton)
        if (press) {
            if (state.getBase() == ButtonState.UNPRESSED) {
                state = ButtonState.HOVER_PRESSED
                allButtons.filter { it != this }.forEach { it.state = ButtonState.UNPRESSED }
            }
        }
        return press
    }
}