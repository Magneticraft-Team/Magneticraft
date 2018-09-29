package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/07/29.
 */
class CompScrollBar(
    override val pos: IVector2,
    override val size: IVector2 = vec2Of(12, 88),
    val texture: ResourceLocation) : IComponent {

    override lateinit var gui: IGui

    companion object {
        val sliderSize = vec2Of(12, 15)
    }

    var tracking = false
    var currentScroll = 0
    var section = 0.0
    var maxScroll = 19

    init {
        recalculateSections()
    }

    fun getScroll(): Int {
        return Math.round(currentScroll.toFloat() / section).toInt()
    }

    fun recalculateSections() {
        section = ((size.yi - sliderSize.yf) / maxScroll).toDouble()
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        gui.drawTexture(DrawableBox(gui.pos + pos + vec2Of(0, currentScroll), sliderSize, vec2Of(232, 0)))

        if (Mouse.isButtonDown(0)) {
            onMouseClick(mouse, 0)
        } else {
            tracking = false
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            if (mouse in Pair(gui.pos + pos, size)) {
                tracking = true
            }
            if (tracking) {
                currentScroll = mouse.yi - pos.yi - gui.pos.yi - 8
                clampScroll()
            }
        }
        return super.onMouseClick(mouse, mouseButton)
    }

    override fun onWheel(amount: Int) {
        if (tracking) {
            return
        }
        currentScroll -= (amount * section).toInt()
        clampScroll()
    }

    fun clampScroll() {
        currentScroll = Math.min(Math.max(0, currentScroll), size.yi - sliderSize.yi)
    }
}