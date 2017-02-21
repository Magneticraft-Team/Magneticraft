package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompElectricBar
import com.cout970.magneticraft.gui.client.components.CompVerticalBar
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.toCelsius
import com.cout970.magneticraft.util.toFahrenheit
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 11/07/2016.
 */
class GuiGrinder(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileGrinder)

    override fun initComponents() {
        components.add(CompBackground("battery"))

        components.add(CompElectricBar(tile.node, Vec2d(47, 64) + box.start))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.heatNode.temperature }, { tile.heatNode.maxTemperature }, { STANDARD_AMBIENT_TEMPERATURE }),
                2, Vec2d(91, 56) + box.start, {
            listOf(
                    if (Config.heatUnitCelsius) {
                        String.format("%.2fC", tile.heatNode.temperature.toCelsius())
                    } else {
                        String.format("%.2fF", tile.heatNode.temperature.toFahrenheit())
                    })
        }))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.production.storage.toDouble() }, { Config.grinderConsumption * 2 }, { 0.0 }),
                3, Vec2d(69, 64) + box.start, { listOf(String.format("%.2fW", tile.production.storage)) }))

        val callback = CallbackBarProvider({ tile.craftingProcess.timer.toDouble() },
                { if (tile.getRecipe() != null) tile.getRecipe()!!.duration.toDouble() else 100.0 }, { 0.0 })
        components.add(CompVerticalBar(callback, 2, Vec2d(80, 64) + box.start, { listOf("Progress: " + "%.1f".format(callback.getLevel() * 100) + "%") }))
    }
}