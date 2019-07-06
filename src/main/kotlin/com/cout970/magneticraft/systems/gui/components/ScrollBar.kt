package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import com.cout970.magneticraft.systems.gui.render.MouseButton
import net.minecraft.client.renderer.GlStateManager.color
import org.lwjgl.input.Mouse
import kotlin.math.max
import kotlin.math.min


/**
 * Created by cout970 on 2017/07/29.
 */
class ScrollBar(
    override val pos: IVector2,
    val sections: Int,
    val id: String = "no-id"
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 = vec2Of(14, 90)

    var tracking = false

    val background = vec2Of(241, 165)
    val sliderSize = vec2Of(12, 15)
    val slider = vec2Of(216, 240)
    val sectionSize = (size.yf - sliderSize.yf - 2) / sections
    var section = 0

    init {
        check(sections <= size.yf - sliderSize.yf)
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        color(1f, 1f, 1f, 1f)
        gui.bindTexture(guiTexture("misc"))
        gui.drawTexture(gui.pos + pos, size, background)

        val scroll = Math.round(section * sectionSize)
        gui.drawTexture(
            gui.pos + pos + vec2Of(1, 1 + scroll),
            sliderSize,
            if (!tracking) slider else vec2Of(228, 240)
        )

        if (Mouse.isButtonDown(MouseButton.LEFT.id)) {
            onMouseClick(mouse, 0)
        } else {
            tracking = false
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            if (mouse in gui.pos + pos to size) {
                tracking = true
            }

            if (tracking) {
                val start = pos.yi + gui.pos.yi
                val offset = mouse.yi - start - sliderSize.yf / 2f
                section = max(0, min(sections, Math.round(offset / sectionSize)))
            }
        }
        return super.onMouseClick(mouse, mouseButton)
    }

    override fun onWheel(amount: Int) {
        if (tracking) {
            return
        }

        section = max(0, min(sections, section - amount))
    }
}