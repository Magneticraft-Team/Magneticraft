package com.cout970.magneticraft.gui.client.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.contains
import com.cout970.magneticraft.util.vector.pos
import com.cout970.magneticraft.util.vector.size
import net.minecraft.util.ResourceLocation

open class AbstractButton(
        val id: Int,
        val texture: ResourceLocation,
        val box: Pair<IVector2, IVector2>,
        val textureSize: IVector2,
        val uvGetter: (ButtonState) -> Pair<IVector2, IVector2>
) : IComponent {

    var listener: IButtonListener? = null
    override val pos: IVector2 = box.pos
    override val size: IVector2 = box.size
    override lateinit var gui: IGui

    var state: ButtonState = ButtonState.UNPRESSED

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        gui.drawTexture(DrawableBox(box, uvGetter(state), textureSize))
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouse in box) {
            val press: Boolean = listener?.invoke(this, mouse, mouseButton) ?: false
            if (press) {
                playSound()
                return true
            }
        }
        return false
    }

    open fun playSound() {}
}