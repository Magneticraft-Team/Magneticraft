package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.offset
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.isMouseButtonDown
import net.minecraft.util.ResourceLocation

class SimpleButton(
    id: Int,
    texture: ResourceLocation?,
    box: Pair<IVector2, IVector2>,
    textureSize: IVector2,
    uvGetter: (ButtonState) -> Pair<IVector2, IVector2>
) : AbstractButton(id, texture, box, textureSize, uvGetter) {

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        if (mouse in box.offset(gui.pos)) {
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

fun buttonUV(offset: IVector2, size: IVector2 = vec2Of(16)): (ButtonState) -> Pair<IVector2, IVector2> = {
    when (it) {
        ButtonState.UNPRESSED -> vec2Of(0, 0)
        ButtonState.HOVER_UNPRESSED -> vec2Of(0, size.y)
        ButtonState.PRESSED, ButtonState.HOVER_PRESSED -> vec2Of(0, size.y * 2)
    } + offset to size
}


fun buttonOf(id: Int = 0, texture: ResourceLocation? = null, pos: IVector2, size: IVector2 = vec2Of(16),
             textureSize: IVector2 = vec2Of(256, 256), uv: IVector2, listener: IButtonListener? = null): SimpleButton {
    return SimpleButton(id, texture, pos to size, textureSize, buttonUV(uv, size)).apply {
        this.listener = listener
    }
}