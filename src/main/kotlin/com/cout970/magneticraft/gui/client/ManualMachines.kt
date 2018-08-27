package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.buttons.ButtonState
import com.cout970.magneticraft.gui.client.components.buttons.IButtonListener
import com.cout970.magneticraft.gui.client.components.buttons.ToggleButton
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerBox
import com.cout970.magneticraft.gui.common.ContainerInserter
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

@Suppress("UNUSED_PARAMETER")
fun guiBox(gui: GuiBase, container: ContainerBox) = gui.run {
    +CompBackground(guiTexture("box"))
}

@Suppress("UNUSED_PARAMETER")
fun guiInserter(gui: GuiBase, container: ContainerInserter) = gui.run {
    +CompBackground(guiTexture("inserter"))

    val mod = container.tile.inserterModule

    +button(0, vec2Of(120, 51), vec2Of(0, 184), container::onClick) { mod.whiteList }
    +button(1, vec2Of(138, 33), vec2Of(18, 166), container::onClick) { mod.useOreDictionary }
    +button(2, vec2Of(120, 33), vec2Of(0, 166), container::onClick) { mod.useMetadata }
    +button(3, vec2Of(138, 51), vec2Of(18, 184), container::onClick) { mod.useNbt }
}

private fun button(id: Int, pos: IVector2, tex: IVector2, listener: IButtonListener, on: () -> Boolean): ToggleButton {

    val uv: (ButtonState) -> Pair<IVector2, IVector2> = { state ->
        when (state) {
            ButtonState.PRESSED, ButtonState.HOVER_PRESSED -> Pair(tex, vec2Of(16))
            else -> Pair(tex + vec2Of(0, 35), vec2Of(16))
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
