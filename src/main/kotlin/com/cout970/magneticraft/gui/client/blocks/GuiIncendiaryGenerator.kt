package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.util.STANDARD_AMBIENT_TEMPERATURE
import com.cout970.magneticraft.util.toCelsius
import com.cout970.magneticraft.util.toFahrenheit
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 08/07/2016.
 */
class GuiIncendiaryGenerator(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground("incendiary_generator"))
        val tile = (container.tileEntity as TileIncendiaryGenerator)

        components.add(CompElectricBar(tile.mainNode, Vec2d(58, 56) + box.start))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.production.storage.toDouble() }, { Config.incendiaryGeneratorMaxProduction.toDouble() }, { 0.0 }),
                3, Vec2d(69, 56) + box.start, { listOf(String.format("%.2fW", tile.production.storage)) }))

        components.add(CompFluidBar(Vec2d(80, 56) + box.start, tile.tank))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.heat.toDouble() }, { TileIncendiaryGenerator.MAX_HEAT }, { STANDARD_AMBIENT_TEMPERATURE }),
                2, Vec2d(91, 56) + box.start, {
            listOf(
                    if (Config.heatUnitCelsius) {
                        String.format("%.2fC", tile.heat.toCelsius())
                    } else {
                        String.format("%.2fF", tile.heat.toFahrenheit())
                    })
        }))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.burningTime.toDouble() }, { Math.max(tile.maxBurningTime.toDouble(), 1.0) }, { 0.0 }),
                1, Vec2d(102, 56) + box.start, { listOf("Fuel") }))


        components.add(CompGreenLight(Vec2d(57, 58) + box.start, { tile.mainNode.voltage < 120 }))
        components.add(CompGreenLight(Vec2d(68, 58) + box.start, { tile.production.storage > 0 }))
        components.add(CompGreenLight(Vec2d(79, 58) + box.start, { tile.tank.clientFluidAmount > 0 }))
        components.add(CompGreenLight(Vec2d(90, 58) + box.start, { tile.heat >= STANDARD_AMBIENT_TEMPERATURE + 5 }))
        components.add(CompGreenLight(Vec2d(101, 58) + box.start, { tile.burningTime > 0 }))
    }
}