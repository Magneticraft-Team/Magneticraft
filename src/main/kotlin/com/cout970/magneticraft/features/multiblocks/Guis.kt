package com.cout970.magneticraft.features.multiblocks

import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.DATA_ID_SELECTED_OPTION
import com.cout970.magneticraft.systems.gui.DATA_ID_SHELVING_UNIT_FILTER
import com.cout970.magneticraft.systems.gui.DATA_ID_SHELVING_UNIT_LEVEL
import com.cout970.magneticraft.systems.gui.GuiBase
import com.cout970.magneticraft.systems.gui.components.*
import com.cout970.magneticraft.systems.gui.components.bars.*
import com.cout970.magneticraft.systems.gui.components.buttons.*
import com.cout970.magneticraft.systems.tilemodules.ModuleShelvingUnitMb

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiShelvingUnit(gui: GuiBase, container: ContainerShelvingUnit) = gui.run {
    var scrollBar: CompScrollBar? = null
    var textInput: CompTextInput? = null
    val texture = guiTexture("shelving_unit")

    sizeX = 194
    sizeY = 207

    +CompBackground(texture, size = vec2Of(194, 207))
    +CompScrollBar(vec2Of(174, 21), texture = texture).apply { scrollBar = this }
    +CompTextInput(fontHelper, vec2Of(10, 7), vec2Of(86, 13)).apply { textInput = this; isFocused = true }
    +CompShelvingUnit(container, scrollBar!!, textInput!!)
    +CompEnableRepeatedEvents()

    @Suppress("UNUSED_PARAMETER")
    fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        val ibd = IBD().apply {
            setInteger(DATA_ID_SHELVING_UNIT_LEVEL, button.id)
            setString(DATA_ID_SHELVING_UNIT_FILTER, "")
        }
        container.sendUpdate(ibd)

        textInput!!.text = ""
        container.filterSlots("")
        container.switchLevel(ModuleShelvingUnitMb.Level.values()[button.id])
        return true
    }

    val buttons = listOf(
        MultiButton(0, texture, vec2Of(176, 129) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 0), vec2Of(23, 24))),
        MultiButton(1, texture, vec2Of(176, 154) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 24 * 3), vec2Of(23, 24))),
        MultiButton(2, texture, vec2Of(176, 179) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 24 * 6), vec2Of(23, 24)))
    )
    buttons.forEach { +it; it.listener = ::onPress; it.allButtons = buttons }

//    +SimpleButton(0, texture,
//        vec2Of(176, 179) to vec2Of(23, 24), vec2Of(256),
//        buttonUV(vec2Of(194, 24 * 6), vec2Of(23, 24))
//    ).also { it.listener = { _, _, _ -> false } }

    val level = container.level.levelIndex
    if (level != -1) {
        buttons[2 - level].state = ButtonState.PRESSED
    }
}

fun guiSolarTower(gui: GuiBase, container: ContainerSolarTower) = gui.run {
    val tile = container.tile

    val texture = guiTexture("solar_tower")

    +CompBackground(texture)
    +buttonOf(pos = vec2Of(107, 43), uv = vec2Of(0, 166), listener = container::onClick)

    +CompHeatBar(tile.node, Vec2d(80, 16))

    val heatReceived = tile.solarTowerModule.production.toBarProvider(500)

    +CompVerticalBar(heatReceived, 3, Vec2d(91, 16), heatReceived.toIntText("Heat received: ", "W"))
}

fun guiHydraulicPress(gui: GuiBase, container: ContainerHydraulicPress) = gui.run {
    val tile = container.tile
    val texture = guiTexture("hydraulic_press")

    +CompBackground(texture)
    +CompElectricBar(tile.node, Vec2d(64, 16))

    val consumption = tile.processModule.consumption.toBarProvider(Config.hydraulicPressMaxConsumption)
    val process = tile.processModule.timedProcess.toBarProvider()

    +CompVerticalBar(consumption, 3, Vec2d(75, 16), consumption.toEnergyText())
    +CompVerticalBar(process, 6, Vec2d(86, 16), process.toPercentText("Processing: "))

    @Suppress("UNUSED_PARAMETER")
    fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        val ibd = IBD().apply {
            setInteger(DATA_ID_SELECTED_OPTION, button.id)
        }
        container.sendUpdate(ibd)
        return true
    }

    val buttons = listOf(
        MultiButton(0, texture, vec2Of(121, 10) to vec2Of(21, 20), uv = buttonUV(vec2Of(0, 166), vec2Of(21, 20))),
        MultiButton(1, texture, vec2Of(121, 30) to vec2Of(21, 20), uv = buttonUV(vec2Of(24, 166), vec2Of(21, 20))),
        MultiButton(2, texture, vec2Of(121, 50) to vec2Of(21, 20), uv = buttonUV(vec2Of(48, 166), vec2Of(21, 20)))
    )
    buttons.forEach { +it; it.listener = ::onPress; it.allButtons = buttons }

    buttons[tile.hydraulicPressModule.mode.ordinal].state = ButtonState.PRESSED
}