package com.cout970.magneticraft.gui.client.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import net.minecraft.util.ResourceLocation
import org.lwjgl.input.Mouse
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/07/29.
 */
class CompScrollBar(val pos: IVector2, val texture: ResourceLocation) : IComponent {

    override val box: Box = Box(pos, vec2Of(12, 88))
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
        section = ((box.size.yi - sliderSize.yf) / maxScroll).toDouble()
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        gui.bindTexture(texture)
        GL11.glColor4f(1f, 1f, 1f, 1f)

        gui.drawScaledTexture(
                pos = gui.box.pos + box.pos + vec2Of(0, currentScroll),
                size = vec2Of(12, 15),
                uvMin = vec2Of(232, 0),
                uvMax = sliderSize,
                textureSize = vec2Of(256, 256)
        )

        if (Mouse.isButtonDown(0)) {
            onMouseClick(mouse, 0)
        } else {
            tracking = false
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        if (mouseButton == 0) {
            if (mouse in Box(gui.box.pos + box.pos, box.size)) {
                tracking = true
            }
            if (tracking) {
                currentScroll = mouse.yi - box.pos.yi - gui.box.pos.yi - 8
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
        currentScroll = Math.min(Math.max(0, currentScroll), box.size.yi - sliderSize.yi)
    }
}