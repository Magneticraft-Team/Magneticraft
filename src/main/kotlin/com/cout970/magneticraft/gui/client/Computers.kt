package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompEnableRepeatedEvents
import com.cout970.magneticraft.gui.client.components.CompLight
import com.cout970.magneticraft.gui.client.components.MonitorComponent
import com.cout970.magneticraft.gui.client.components.bars.CompElectricBar
import com.cout970.magneticraft.gui.client.components.bars.CompStorageBar
import com.cout970.magneticraft.gui.client.components.buttons.buttonOf
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerComputer
import com.cout970.magneticraft.gui.common.ContainerMiningRobot
import com.cout970.magneticraft.gui.common.core.DATA_ID_COMPUTER_BUTTON
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiComputer(gui: GuiBase, container: ContainerComputer) = gui.run {
    val monitor = container.monitor
    val motherboard = container.motherboard
    val texture = guiTexture("old_monitor")
    val textureSize = Vec2d(512, 512)
    val buttonSize = vec2Of(8, 8)

    sizeX = 350
    sizeY = 255

    +CompBackground(texture, textureSize = textureSize, size = Vec2d(350, 230))
    +MonitorComponent(monitor, true)

    listOf(
            buttonOf(0, texture, vec2Of(23, 220), buttonSize, textureSize, vec2Of(0, 230)),
            buttonOf(1, texture, vec2Of(33, 220), buttonSize, textureSize, vec2Of(8, 230)),
            buttonOf(2, texture, vec2Of(43, 220), buttonSize, textureSize, vec2Of(16, 230))
    ).forEach {
        it.listener = { btn, _, _ ->
            val ibd = IBD().apply { setInteger(DATA_ID_COMPUTER_BUTTON, btn.id) }
            container.sendUpdate(ibd)
            true
        }
        +it
    }

    +CompLight(
            on = DrawableBox(pos + Vec2d(14, 221), vec2Of(7, 7), vec2Of(24, 230), vec2Of(7, 7), textureSize),
            off = DrawableBox(pos + Vec2d(14, 221), vec2Of(7, 7), vec2Of(24, 237), vec2Of(7, 7), textureSize),
            texture = texture, condition = motherboard::isOnline
    )

    +CompEnableRepeatedEvents()
}

fun guiMiningRobot(gui: GuiBase, container: ContainerMiningRobot) = gui.run {
    val tile = container.tile
    val monitor = container.monitor
    val motherboard = container.motherboard
    val texture = guiTexture("mining_robot")
    val textureSize = Vec2d(512, 512)
    val buttonSize = vec2Of(8, 8)

    sizeX = 350
    sizeY = 317

    +CompBackground(texture, textureSize = textureSize, size = Vec2d(350, 317))
    +MonitorComponent(monitor, false)
    +CompElectricBar(tile.node, Vec2d(10, 238))

    listOf(
            buttonOf(0, texture, vec2Of(23, 220), buttonSize, textureSize, vec2Of(0, 318)),
            buttonOf(1, texture, vec2Of(33, 220), buttonSize, textureSize, vec2Of(8, 318)),
            buttonOf(2, texture, vec2Of(43, 220), buttonSize, textureSize, vec2Of(16, 318))
    ).forEach {
        it.listener = { btn, _, _ ->
            val ibd = IBD().apply { setInteger(DATA_ID_COMPUTER_BUTTON, btn.id) }
            container.sendUpdate(ibd)
            true
        }
        +it
    }

    +CompStorageBar(tile.energyStorage, vec2Of(19, 238), vec2Of(31, 317), texture, textureSize)

    +CompLight(
            on = DrawableBox(pos + Vec2d(14, 221), vec2Of(7, 7), vec2Of(7, 325), vec2Of(7, 7), textureSize),
            off = DrawableBox(pos + Vec2d(14, 221), vec2Of(7, 7), vec2Of(0, 325), vec2Of(7, 7), textureSize),
            texture = texture, condition = { motherboard.isOnline() }
    )
    +CompEnableRepeatedEvents()
}