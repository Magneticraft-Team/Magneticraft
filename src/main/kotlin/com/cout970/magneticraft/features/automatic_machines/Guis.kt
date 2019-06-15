package com.cout970.magneticraft.features.automatic_machines

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.components.CompBackground
import com.cout970.magneticraft.systems.gui.components.buttons.ButtonState
import com.cout970.magneticraft.systems.gui.components.buttons.IButtonListener
import com.cout970.magneticraft.systems.gui.components.buttons.ToggleButton
import com.cout970.magneticraft.systems.gui.render.GuiBase

@Suppress("UNUSED_PARAMETER")
fun guiInserter(gui: GuiBase, container: ContainerInserter) = gui.run {
    +CompBackground(guiTexture("inserter"))

    val mod = container.tile.inserterModule

    +button(0, vec2Of(120, 50), vec2Of(0, 202), container::onClick) { mod.whiteList }
    +button(1, vec2Of(138, 32), vec2Of(18, 184), container::onClick) { mod.useOreDictionary }
    +button(2, vec2Of(120, 32), vec2Of(0, 184), container::onClick) { mod.useMetadata }
    +button(3, vec2Of(138, 50), vec2Of(18, 202), container::onClick) { mod.useNbt }
    +button(4, vec2Of(120, 14), vec2Of(0, 166), container::onClick) { mod.canDropItems }
    +button(5, vec2Of(138, 14), vec2Of(18, 166), container::onClick) { mod.canGrabItems }
}

@Suppress("UNUSED_PARAMETER")
fun guiRelay(gui: GuiBase, container: ContainerRelay) = gui.run {
    +CompBackground(guiTexture("relay"))
}

@Suppress("UNUSED_PARAMETER")
fun guiFilter(gui: GuiBase, container: ContainerFilter) = gui.run {
    +CompBackground(guiTexture("relay"))
}

private fun button(id: Int, pos: IVector2, tex: IVector2, listener: IButtonListener, on: () -> Boolean): ToggleButton {

    val uv: (ButtonState) -> Pair<IVector2, IVector2> = { state ->
        when (state) {
            ButtonState.PRESSED, ButtonState.HOVER_PRESSED -> Pair(tex, vec2Of(16))
            else -> Pair(tex + vec2Of(35, 0), vec2Of(16))
        }
    }

    return object : ToggleButton(id, guiTexture("inserter"), pos to vec2Of(16), vec2Of(256), uv) {
        init {
            this.listener = listener
        }

        override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
            state = if (on()) ButtonState.PRESSED else ButtonState.UNPRESSED
            super.drawFirstLayer(mouse, partialTicks)
        }
    }
}