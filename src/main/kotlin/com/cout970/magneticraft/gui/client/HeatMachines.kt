package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerCombustionChamber
import com.cout970.magneticraft.gui.common.ContainerSteamBoiler
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.WATER_BOILING_POINT
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

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

fun guiSteamBoiler(gui: GuiBase, container: ContainerSteamBoiler) = gui.run {
    val tile = container.tile
    val texture = guiTexture("steam_boiler")

    +CompBackground(texture)

    val production = tile.boilerModule.production.toBarProvider(tile.boilerModule.maxProduction)

    +CompVerticalBar(production, 3, Vec2d(69, 16), production.toIntText(postfix = " mB/t"))

    +CompFluidBar(vec2Of(80, 16), texture, vec2Of(0, 166), tile.waterTank)
    +CompFluidBar(vec2Of(102, 16), texture, vec2Of(0, 166), tile.steamTank)
}