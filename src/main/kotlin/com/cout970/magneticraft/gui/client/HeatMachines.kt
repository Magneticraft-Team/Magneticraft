package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.bars.CompVerticalBar
import com.cout970.magneticraft.gui.client.components.bars.toHeatText
import com.cout970.magneticraft.gui.client.components.bars.toPercentText
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerCombustionChamber
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.WATER_BOILING_POINT
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiCombustionChamber(gui: GuiBase, container: ContainerCombustionChamber) = gui.run {
    val tile = container.tile

    +CompBackground(guiTexture("combustion_chamber"))

    val burningCallback = CallbackBarProvider(
            { tile.combustionChamberModule.maxBurningTime - tile.combustionChamberModule.burningTime.toDouble() },
            tile.combustionChamberModule::maxBurningTime, { 0.0 })

    val heatCallback = CallbackBarProvider(tile.combustionChamberModule::heat,
            ::WATER_BOILING_POINT, ::STANDARD_AMBIENT_TEMPERATURE)

    +CompVerticalBar(burningCallback, 1, Vec2d(69, 16), burningCallback.toPercentText("Fuel: "))
    +CompVerticalBar(heatCallback, 2, Vec2d(80, 16), heatCallback.toHeatText())
}