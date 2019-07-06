package com.cout970.magneticraft.systems.gui.components.buttons

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.contains
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.renderer.GlStateManager.color
import net.minecraft.client.renderer.GlStateManager.enableBlend

class SelectButton(
    override val pos: IVector2,
    val options: List<SelectOption>,
    val id: String = "no-id",
    var onClick: (Int) -> Unit = {},
    var selectedOption: () -> Int = { 0 }
) : IComponent {

    override lateinit var gui: IGui
    override val size: IVector2 = vec2Of(18, 18)
    val backgroundEnable = vec2Of(36, 100)
    val backgroundDisable = vec2Of(55, 100)
    val hoverUV = vec2Of(183, 10)

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val selected = selectedOption()
        gui.bindTexture(guiTexture("misc"))

        for ((index, option) in options.withIndex()) {
            gui.drawTexture(
                gui.pos + pos + option.offset,
                size,
                if (selected == index) backgroundEnable else backgroundDisable
            )

            gui.drawTexture(
                gui.pos + pos + option.offset + 1,
                size - 2,
                option.background
            )

            if (selected != index) {
                val start = gui.pos + pos + option.offset + 1
                val end = start + size - 2
                gui.drawColor(start, end, 0x40000000)
                enableBlend()
                color(1f, 1f, 1f, 1f)
            }
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        for (option in options) {

            if (mouse in (gui.pos + pos + option.offset to size)) {
                gui.bindTexture(guiTexture("misc"))
                gui.drawTexture(pos + option.offset, size, hoverUV)

                gui.drawHoveringText(listOf(option.tooltip), mouse)
            }
        }
    }

    override fun onMouseClick(mouse: Vec2d, mouseButton: Int): Boolean {
        for ((index, option) in options.withIndex()) {

            val inBounds = mouse in (gui.pos + pos + option.offset to size)
            if (inBounds) {
                onClick(index)
                return true
            }
        }
        return false
    }

    class SelectOption(
        val offset: IVector2,
        val background: IVector2,
        val tooltip: String
    )
}