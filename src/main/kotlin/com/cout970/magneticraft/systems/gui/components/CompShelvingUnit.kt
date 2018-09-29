package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.features.multiblocks.ContainerShelvingUnit
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.DATA_ID_SHELVING_UNIT_SCROLL
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11


/**
 * Created by cout970 on 2017/07/29.
 */
class CompShelvingUnit(
    val container: ContainerShelvingUnit,
    val scrollBar: CompScrollBar,
    val textInput: CompTextInput
) : IComponent {

    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d.ZERO
    override lateinit var gui: IGui

    init {
        textInput.updateFunc = { container.setFilter(it.text) }
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val scroll = scrollBar.getScroll() / 19f
        if (container.scroll != scroll) {
            container.withScroll(scroll)
            container.sendUpdate(IBD().apply { setFloat(DATA_ID_SHELVING_UNIT_SCROLL, scroll) })
        }
        val column = Math.max(0, Math.round(scroll * ((container.currentSlots.size / 9f) - 5)))
        gui.bindTexture(guiTexture("shelving_unit"))

        GL11.glColor4f(1f, 1f, 1f, 1f)
        GlStateManager.enableBlend()

        (0 until 5 * 9).forEach {
            val pos = it + column * 9
            if (pos >= container.currentSlots.size) {
                val x = it % 9 * 18 + 8
                val y = it / 9 * 18 + 21

                gui.drawTexture(DrawableBox(gui.pos + vec2Of(x, y), vec2Of(16, 16), vec2Of(240, 15)))
            }
        }
        if (container.currentSlots.isEmpty() && (container.filterText.isEmpty() || container.filterText.isBlank())) {
            gui.drawColor(Pair(gui.pos + vec2Of(6, 56), vec2Of(164, 17)), 0xF0000000.toInt())
            gui.drawCenteredString("Add Chests to increase storage", gui.pos + vec2Of(88, 61),
                0xFFFFFFFF.toInt())
        }
        GlStateManager.disableBlend()
        GL11.glColor4f(1f, 1f, 1f, 1f)
    }
}