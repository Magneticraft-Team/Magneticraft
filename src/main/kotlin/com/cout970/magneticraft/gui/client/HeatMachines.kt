package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.bars.CompVerticalBar
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.formatHeat
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.toKelvinFromCelsius
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiCombustionChamber(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileCombustionChamber)

    override fun initComponents() {
        components.add(CompBackground(guiTexture("combustion_chamber")))
        val burningCallback = CallbackBarProvider(
                { tile.combustionChamberModule.maxBurningTime.toDouble() - tile.combustionChamberModule.burningTime.toDouble() },
                { tile.combustionChamberModule.maxBurningTime.toDouble() },
                { 0.0 }
        )
        components.add(
                CompVerticalBar(burningCallback, 1, Vec2d(69, 16),
                        { listOf(String.format("Fuel: %.1f%%", burningCallback.getLevel() * 100)) }))

        val heatCallback = CallbackBarProvider(
                { tile.combustionChamberModule.heat.toDouble() },
                { 99.5.toKelvinFromCelsius() },
                { 24.0.toKelvinFromCelsius() }
        )
        components.add(
                CompVerticalBar(heatCallback, 2, Vec2d(80, 16),
                        { listOf("Heat: " + formatHeat(heatCallback.callback())) }))
    }
}