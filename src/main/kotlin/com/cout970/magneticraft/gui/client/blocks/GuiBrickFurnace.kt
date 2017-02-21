package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompVerticalBar
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.tileentity.heat.TileBrickFurnace
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.toCelsius
import com.cout970.magneticraft.util.toFahrenheit
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 22/07/2016.
 */
class GuiBrickFurnace(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileBrickFurnace)

    override fun initComponents() {
        components.add(CompBackground("electric_furnace"))

        val callback = CallbackBarProvider({ tile.burningTime.toDouble() }, { TileBrickFurnace.MAX_BURNING_TIME.toDouble() }, { 0.0 })
        components.add(CompVerticalBar(callback, 2, Vec2d(80, 64) + box.start, { listOf("Burning: " + "%.1f".format(callback.getLevel() * 100) + "%") }))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.heat.temperature }, { tile.heat.maxTemperature }, { STANDARD_AMBIENT_TEMPERATURE }),
                2, Vec2d(91, 56) + box.start, {
            listOf(
                    if (Config.heatUnitCelsius) {
                        String.format("%.2fC", tile.heat.temperature.toCelsius())
                    } else {
                        String.format("%.2fF", tile.heat.temperature.toFahrenheit())
                    })
        }))
    }
}