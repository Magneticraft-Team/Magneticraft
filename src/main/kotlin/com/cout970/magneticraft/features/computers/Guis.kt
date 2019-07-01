package com.cout970.magneticraft.features.computers

import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.gui.DATA_ID_COMPUTER_BUTTON
import com.cout970.magneticraft.systems.gui.components.CompBackground
import com.cout970.magneticraft.systems.gui.components.CompEnableRepeatedEvents
import com.cout970.magneticraft.systems.gui.components.CompLight
import com.cout970.magneticraft.systems.gui.components.MonitorComponent
import com.cout970.magneticraft.systems.gui.components.bars.CompElectricBar
import com.cout970.magneticraft.systems.gui.components.bars.CompStorageBar
import com.cout970.magneticraft.systems.gui.components.buttons.buttonOf
import com.cout970.magneticraft.systems.gui.render.DrawableBox
import com.cout970.magneticraft.systems.gui.render.GuiBase

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiComputer(gui: GuiBase, container: ContainerComputer) = gui.run {
    val motherboard = container.motherboard
    val texture = guiTexture("old_monitor")
    val textureSize = vec2Of(512, 512)

    sizeX = 350
    sizeY = 250

    +CompBackground(texture, textureSize = textureSize, size = vec2Of(350, 250))
    +MonitorComponent(container.tile.ref, container.monitor, container.keyboard, container)

    +buttonOf(0, texture, vec2Of(12, 225), vec2Of(16, 16), textureSize, vec2Of(36, 251)) { _, _, _ ->
        val ibd = IBD().apply { setInteger(DATA_ID_COMPUTER_BUTTON, 0) }
        container.sendUpdate(ibd)
        true
    }

    val pos = vec2Of((width - sizeX) / 2, (height - sizeY) / 2) + vec2Of(12, 225)

    +CompLight(
        on = DrawableBox(pos, vec2Of(16, 16), vec2Of(1, 251), vec2Of(16, 16), textureSize),
        off = DrawableBox(pos, vec2Of(16, 16), vec2Of(19, 251), vec2Of(16, 16), textureSize),
        texture = texture, condition = motherboard::isOnline
    )

    +CompEnableRepeatedEvents()
}

fun guiMiningRobot(gui: GuiBase, container: ContainerMiningRobot) = gui.run {
    val tile = container.tile
    val motherboard = container.motherboard
    val texture = guiTexture("mining_robot")
    val textureSize = vec2Of(512, 512)

    sizeX = 350
    sizeY = 308

    +CompBackground(texture, textureSize = textureSize, size = vec2Of(350, 308))
    +MonitorComponent(tile.ref, container.monitor, container.keyboard, container)
    +CompElectricBar(tile.node, vec2Of(39, 252))

    +buttonOf(0, texture, vec2Of(69, 242), vec2Of(16, 16), textureSize, vec2Of(0, 326)) { _, _, _ ->
        val ibd = IBD().apply { setInteger(DATA_ID_COMPUTER_BUTTON, 0) }
        container.sendUpdate(ibd)
        true
    }

    +CompStorageBar(tile.energyStorage, vec2Of(48, 252), vec2Of(36, 308), texture, textureSize)

    val pos = vec2Of((width - sizeX) / 2, (height - sizeY) / 2) + vec2Of(69, 242)

    +CompLight(
        on = DrawableBox(pos, vec2Of(16, 16), vec2Of(1, 309), vec2Of(16, 16), textureSize),
        off = DrawableBox(pos, vec2Of(16, 16), vec2Of(19, 309), vec2Of(16, 16), textureSize),
        texture = texture, condition = motherboard::isOnline
    )

    +CompEnableRepeatedEvents()
}