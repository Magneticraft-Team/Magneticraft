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
    val motherboard = container.motherboard
    val texture = guiTexture("old_monitor")
    val textureSize = Vec2d(512, 512)
    val buttonSize = vec2Of(8, 8)

    sizeX = 350
    sizeY = 255

    +CompBackground(texture, textureSize = textureSize, size = Vec2d(350, 230))
    +MonitorComponent(container.tile.ref, container.monitor, container, true)

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

    val size = vec2Of(7, 7)
    val pos = Vec2d((width - sizeX) / 2, (height - sizeY) / 2) + Vec2d(14, 220)

    +CompLight(
            on = DrawableBox(pos, size, vec2Of(24, 237), size, textureSize),
            off = DrawableBox(pos, size, vec2Of(24, 230), size, textureSize),
            texture = texture, condition = motherboard::isOnline
    )

    +CompEnableRepeatedEvents()
}

fun guiMiningRobot(gui: GuiBase, container: ContainerMiningRobot) = gui.run {
    val tile = container.tile
    val motherboard = container.motherboard
    val texture = guiTexture("mining_robot")
    val textureSize = Vec2d(512, 512)
    val buttonSize = vec2Of(8, 8)

    sizeX = 350
    sizeY = 317

    +CompBackground(texture, textureSize = textureSize, size = Vec2d(350, 317))
    +MonitorComponent(tile.ref, container.monitor, container, false)
    +CompElectricBar(tile.node, Vec2d(10, 238))

    listOf(
            buttonOf(0, texture, vec2Of(23, 220), buttonSize, textureSize, vec2Of(0, 317)),
            buttonOf(1, texture, vec2Of(33, 220), buttonSize, textureSize, vec2Of(8, 317)),
            buttonOf(2, texture, vec2Of(43, 220), buttonSize, textureSize, vec2Of(16, 317))
    ).forEach {
        it.listener = { btn, _, _ ->
            val ibd = IBD().apply { setInteger(DATA_ID_COMPUTER_BUTTON, btn.id) }
            container.sendUpdate(ibd)
            true
        }
        +it
    }

    +CompStorageBar(tile.energyStorage, vec2Of(19, 238), vec2Of(31, 317), texture, textureSize)

    val size = vec2Of(7, 7)
    val pos = Vec2d((width - sizeX) / 2, (height - sizeY) / 2) + Vec2d(14, 220)

    +CompLight(
            on = DrawableBox(pos, size, vec2Of(24, 324), size, textureSize),
            off = DrawableBox(pos, size, vec2Of(24, 317), size, textureSize),
            texture = texture, condition = { motherboard.isOnline() }
    )
    +CompEnableRepeatedEvents()
}