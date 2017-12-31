package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerBattery
import com.cout970.magneticraft.gui.common.ContainerElectricFurnace
import com.cout970.magneticraft.gui.common.ContainerThermopile
import com.cout970.magneticraft.gui.common.ContainerWindTurbine
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiElectricFurnace(container: ContainerElectricFurnace) : GuiBase(container) {

    val tile = container.tile

    override fun initComponents() {
        components.add(CompBackground(guiTexture("electric_furnace")))
        components.add(CompElectricBar(tile.node, Vec2d(58, 16)))
        val consumptionCallback = CallbackBarProvider(
                callback = { tile.processModule.production.storage.toDouble() },
                max = { Config.electricFurnaceMaxConsumption },
                min = { 0.0 }
        )
        components.add(CompVerticalBar(consumptionCallback, 3,
                Vec2d(69, 16),
                { listOf(String.format("%.2fW", consumptionCallback.callback())) }))

        val processCallback = CallbackBarProvider(
                { tile.processModule.timedProcess.timer.toDouble() },
                { tile.processModule.timedProcess.limit().toDouble() },
                { 0.0 }
        )
        components.add(CompVerticalBar(processCallback, 2, Vec2d(80, 16),
                { listOf("Burning: " + "%.1f".format(processCallback.getLevel() * 100) + "%") }))
    }
}

class GuiBattery(container: ContainerBattery) : GuiBase(container) {

    val tile = container.tile

    override fun initComponents() {
        components.add(CompBackground(guiTexture("battery")))
        components.add(CompElectricBar(tile.node, Vec2d(47, 16)))

        components.add(BatteryStorageBar(this, guiTexture("battery"),
                tile.storageModule))

        components.add(TransferRateBar(
                value = { tile.storageModule.chargeRate.storage.toDouble() },
                min = { -tile.storageModule.maxChargeSpeed },
                base = { 0.0 },
                max = { tile.storageModule.maxChargeSpeed }, pos = Vec2d(58, 16))
        )

        components.add(TransferRateBar(
                { tile.itemChargeModule.itemChargeRate.storage.toDouble() },
                { -tile.itemChargeModule.transferRate.toDouble() },
                { 0.0 },
                { tile.itemChargeModule.transferRate.toDouble() }, Vec2d(58 + 33, 16))
        )
    }
}

class GuiThermopile(container: ContainerThermopile) : GuiBase(container) {

    val tile = container.tile

    override fun initComponents() {
        components.add(CompBackground(guiTexture("thermopile")))
        components.add(CompElectricBar(tile.node, Vec2d(64, 17)))
        components.add(CompStorageBar(tile.storageModule, Vec2d(73, 17), vec2Of(0, 166), guiTexture("thermopile")))

        val production = StaticBarProvider(0.0, Config.thermopileProduction) {
            tile.thermopileModule.production.storage.toDouble()
        }

        components.add(CompVerticalBar(production, 3, Vec2d(89, 17),
                { listOf(String.format("%.2fW", production.callback())) }))

        val source = StaticBarProvider(0.0, 300.0) { tile.thermopileModule.heatSource.toDouble() }

        components.add(CompVerticalBar(source, 2, Vec2d(98, 17),
                { listOf(source.callback().toInt().toString()) }))

        val drain = StaticBarProvider(0.0, 300.0) { tile.thermopileModule.heatDrain.toDouble() }

        components.add(CompVerticalBar(drain, 5, Vec2d(107, 17),
                { listOf(drain.callback().toInt().toString()) }))

    }
}

class GuiWindTurbine(container: ContainerWindTurbine) : GuiBase(container) {

    val tile = container.tile

    override fun initComponents() {
        components.add(CompBackground(guiTexture("wind_turbine")))
        components.add(CompElectricBar(tile.node, Vec2d(74, 16)))

        val openSpace = StaticBarProvider(0.0, 1.0) {
            tile.windTurbineModule.openSpace.toDouble()
        }

        components.add(CompVerticalBar(openSpace, 3, Vec2d(85, 16),
                { listOf("Wind not blocked: ${(openSpace.callback() * 100).toInt()}%") }))

        val wind = StaticBarProvider(0.0, 1.0) {
            tile.windTurbineModule.currentWind.toDouble()
        }

        components.add(CompVerticalBar(wind, 2, Vec2d(96, 16),
                { listOf("Wind: ${(wind.callback() * 100).toInt()}%") }))

    }
}