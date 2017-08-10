package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompScrollBar
import com.cout970.magneticraft.gui.client.components.CompShelvingUnit
import com.cout970.magneticraft.gui.client.components.CompTextInput
import com.cout970.magneticraft.gui.client.components.buttons.AbstractButton
import com.cout970.magneticraft.gui.client.components.buttons.ButtonState
import com.cout970.magneticraft.gui.client.components.buttons.MultiButton
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerShelvingUnit
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnit
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import org.lwjgl.input.Keyboard

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiShelvingUnit(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        xSize = 194
        ySize = 207
        val texture = guiTexture("shelving_unit")
        components.add(CompBackground(texture, size = vec2Of(194, 207)))
        val scrollBar = CompScrollBar(vec2Of(174, 21), texture = texture)
        val textInput = CompTextInput(fontRenderer, vec2Of(10, 7), vec2Of(86, 13))
        val button1Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 75) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 75) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 0) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 0) to vec2Of(23, 24))
        )
        val button2Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 100) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 100) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 25) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 25) to vec2Of(23, 24))
        )
        val button3Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 125) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 125) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 50) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 50) to vec2Of(23, 24))
        )
        val buttons = listOf(
                MultiButton(2, texture, vec2Of(176, 129) to vec2Of(23, 24), vec2Of(256), { button1Map[it]!! }),
                MultiButton(1, texture, vec2Of(176, 154) to vec2Of(23, 24), vec2Of(256), { button2Map[it]!! }),
                MultiButton(0, texture, vec2Of(176, 179) to vec2Of(23, 24), vec2Of(256), { button3Map[it]!! })
        )
        components.add(scrollBar)
        components.add(textInput)
        buttons.forEach { components.add(it); it.listener = this::onPress; it.allButtons = buttons }
        components.add(CompShelvingUnit(container as ContainerShelvingUnit, scrollBar, textInput))

        (container as? ContainerShelvingUnit)?.let {
            buttons[2 - it.level.levelIndex].state = ButtonState.PRESSED
        }
        Keyboard.enableRepeatEvents(true)
    }

    fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        val ibd = IBD().apply { setInteger(2, button.id) }
        container.sendUpdate(ibd)
        (container as ContainerShelvingUnit).switchLevel(ModuleShelvingUnit.Level.values()[button.id])
        return true
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        super.onGuiClosed()
    }
}

