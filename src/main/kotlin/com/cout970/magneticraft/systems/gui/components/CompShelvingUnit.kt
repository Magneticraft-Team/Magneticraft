package com.cout970.magneticraft.systems.gui.components

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.features.multiblocks.ContainerShelvingUnit
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.AutoGui
import com.cout970.magneticraft.systems.gui.DATA_ID_SHELVING_UNIT_SCROLL
import com.cout970.magneticraft.systems.gui.GuiBase
import com.cout970.magneticraft.systems.gui.components.buttons.SelectButton
import com.cout970.magneticraft.systems.gui.render.IComponent
import com.cout970.magneticraft.systems.gui.render.IGui
import net.minecraft.client.renderer.GlStateManager.color
import net.minecraft.client.renderer.GlStateManager.enableBlend
import net.minecraft.client.resources.I18n


/**
 * Created by cout970 on 2017/07/29.
 */
class ComponentShelvingUnit : IComponent {
    override val pos: IVector2 = Vec2d.ZERO
    override val size: IVector2 = Vec2d.ZERO
    override lateinit var gui: IGui

    lateinit var scrollBar: ScrollBar
    lateinit var searchBar: SearchBar
    lateinit var container: ContainerShelvingUnit
    var lastScroll = 0

    override fun init() {
        container = (gui as GuiBase).container as ContainerShelvingUnit
        scrollBar = gui.components.filterIsInstance<ScrollBar>().first()
        searchBar = gui.components.filterIsInstance<SearchBar>().first()
        searchBar.onChange = container::setFilter

        val select = gui.components.filterIsInstance<SelectButton>().first()
        val oldCallback = select.onClick

        select.onClick = {
            searchBar.textField.text = ""
            oldCallback(it)
        }
    }

    override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
        val scroll = scrollBar.section
        val auto = gui as AutoGui
        auto.slots = auto.createSlots(container.inventorySlots)

        if (lastScroll != scroll) {
            lastScroll = scroll
            val scrollPercent = scroll / scrollBar.sections.toFloat()
            container.withScroll(scrollPercent)
            container.sendUpdate(IBD().setFloat(DATA_ID_SHELVING_UNIT_SCROLL, scrollPercent))
        }
    }

    override fun drawSecondLayer(mouse: Vec2d) {
        val scrollPercent = scrollBar.section / scrollBar.sections.toFloat()
        val columnIndex = scrollPercent * ((container.currentSlots.size / 9f) - 5)
        val column = Math.max(0, Math.round(columnIndex))

        gui.bindTexture(guiTexture("misc"))

        (0 until 5 * 9).forEach {
            val pos = it + column * 9
            if (pos >= container.currentSlots.size) {
                val x = it % 9 * 18 + 7
                val y = it / 9 * 18 + 20

                gui.drawTexture(
                    vec2Of(x, y),
                    vec2Of(18, 18),
                    vec2Of(180, 69)
                )
            }
        }

        val filterText = searchBar.textField.text
        if (container.currentSlots.isEmpty() && (filterText.isEmpty() || filterText.isBlank())) {
            val start = vec2Of(6, 56)
            val end = start + vec2Of(164, 17)

            color(1f, 1f, 1f, 1f)
            gui.drawColor(start, end, 0xF0000000.toInt())
            gui.drawCenteredString(
                text = I18n.format("text.magneticraft.shelving_unit_add_chests"),
                pos = vec2Of(88, 61),
                color = 0xFFFFFFFF.toInt()
            )
            enableBlend()
            color(1f, 1f, 1f, 1f)
        }
    }
}