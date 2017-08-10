package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.IVector2
import com.cout970.magneticraft.computer.DeviceMonitor
import com.cout970.magneticraft.computer.Motherboard
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompLight
import com.cout970.magneticraft.gui.client.components.MonitorComponent
import com.cout970.magneticraft.gui.client.components.buttons.AbstractButton
import com.cout970.magneticraft.gui.client.components.buttons.ButtonState
import com.cout970.magneticraft.gui.client.components.buttons.SimpleButton
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerComputer
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiComputer(container: ContainerBase) : GuiBase(container) {

    val monitor: DeviceMonitor = (container as ContainerComputer).monitor
    val motherboard: Motherboard = (container as ContainerComputer).motherboard

    init {
        xSize = 350
        ySize = 255
    }

    override fun initComponents() {
        val texture = guiTexture("old_monitor")
        val textureSize = Vec2d(512, 512)
        val buttonSize = vec2Of(8, 8)
        components.add(CompBackground(texture, textureSize = textureSize, size = Vec2d(350, 230)))
        components.add(MonitorComponent(monitor))
        components.add(SimpleButton(0, texture, vec2Of(23, 220) to buttonSize, textureSize, this::getUV))
        components.add(SimpleButton(1, texture, vec2Of(33, 220) to buttonSize, textureSize, this::getUV))
        components.add(SimpleButton(2, texture, vec2Of(43, 220) to buttonSize, textureSize, this::getUV))

        components.add(CompLight(
                on = DrawableBox(pos + Vec2d(14, 221) to vec2Of(7, 7), vec2Of(7, 238) to vec2Of(7, 7), textureSize),
                off = DrawableBox(pos + Vec2d(14, 221) to vec2Of(7, 7), vec2Of(0, 238) to vec2Of(7, 7), textureSize),
                texture = texture, condition = { motherboard.isOnline() }
        ))
        components.filterIsInstance<SimpleButton>().forEach { it.listener = this::onPress }
    }

    fun getUV(state: ButtonState): Pair<IVector2, IVector2> {
        return when (state) {
            ButtonState.UNPRESSED -> vec2Of(0, 230) to vec2Of(8)
            ButtonState.PRESSED -> vec2Of(16, 230) to vec2Of(8)
            ButtonState.HOVER_UNPRESSED -> vec2Of(8, 230) to vec2Of(8)
            ButtonState.HOVER_PRESSED -> vec2Of(16, 230) to vec2Of(8)
        }
    }

    fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        val ibd = IBD().apply { setInteger(50, button.id) }
        container.sendUpdate(ibd)
        return true
    }
}