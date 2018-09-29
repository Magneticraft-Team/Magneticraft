package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.pos
import com.cout970.magneticraft.misc.vector.size
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.util.ResourceLocation

open class AbstractButton(
    val id: Int,
    val texture: ResourceLocation?,
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
        texture?.let { gui.bindTexture(it) }
        val uv = uvGetter(state)
        gui.drawTexture(DrawableBox(gui.pos + box.pos, box.size, uv.pos, uv.second, textureSize))
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouse in (gui.pos + box.pos to box.size)) {
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