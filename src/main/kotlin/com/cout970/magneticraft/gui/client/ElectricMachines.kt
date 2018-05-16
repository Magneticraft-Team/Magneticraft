package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiElectricFurnace(gui: GuiBase, container: ContainerElectricFurnace) = gui.run {
    val tile = container.tile
    +CompBackground(guiTexture("electric_furnace"))
    +CompElectricBar(tile.node, Vec2d(58, 16))

    val consumptionCallback = CallbackBarProvider(
            callback = { tile.processModule.consumption.storage.toDouble() },
            max = { Config.electricFurnaceMaxConsumption },
            min = { 0.0 }
    )

    val processCallback = CallbackBarProvider(
            { tile.processModule.timedProcess.timer.toDouble() },
            { tile.processModule.timedProcess.limit().toDouble() },
            { 0.0 }
    )

    +CompVerticalBar(consumptionCallback, 3, Vec2d(69, 16), consumptionCallback.toEnergyText())
    +CompVerticalBar(processCallback, 2, Vec2d(80, 16), processCallback.toPercentText("Burning: "))
}

fun guiBattery(gui: GuiBase, container: ContainerBattery) = gui.run {
    val tile = container.tile
    +CompBackground(guiTexture("battery"))
    +CompElectricBar(tile.node, Vec2d(47, 16))

    +BatteryStorageBar(this, guiTexture("battery"), tile.storageModule)

    +TransferRateBar(
            value = { tile.storageModule.chargeRate.storage.toDouble() },
            min = { -tile.storageModule.maxChargeSpeed },
            base = { 0.0 },
            max = { tile.storageModule.maxChargeSpeed }, pos = Vec2d(58, 16))

    +TransferRateBar(
            value = { tile.itemChargeModule.itemChargeRate.storage.toDouble() },
            min = { -tile.itemChargeModule.transferRate.toDouble() },
            base = { 0.0 },
            max = { tile.itemChargeModule.transferRate.toDouble() }, pos = Vec2d(58 + 33, 16)
    )
}

fun guiThermopile(gui: GuiBase, container: ContainerThermopile) = gui.run {
    val tile = container.tile
    val texture = guiTexture("thermopile")

    +CompBackground(texture)
    +CompElectricBar(tile.node, Vec2d(64, 17))
    +CompStorageBar(tile.storageModule, Vec2d(73, 17), vec2Of(0, 166), texture)

    val production = StaticBarProvider(0.0, Config.thermopileProduction,
            tile.thermopileModule.production::storage)

    +CompVerticalBar(production, 3, Vec2d(89, 17), production.toEnergyText())

    val source = StaticBarProvider(0.0, 300.0, tile.thermopileModule::heatSource)
    val drain = StaticBarProvider(0.0, 300.0, tile.thermopileModule::heatDrain)

    +CompVerticalBar(source, 2, Vec2d(98, 17), source.toIntText())
    +CompVerticalBar(drain, 5, Vec2d(107, 17), drain.toIntText())
}

fun guiWindTurbine(gui: GuiBase, container: ContainerWindTurbine) = gui.run {
    val tile = container.tile
    val texture = guiTexture("wind_turbine")

    +CompBackground(texture)
    +CompElectricBar(tile.node, Vec2d(64, 17))
    +CompStorageBar(tile.storageModule, Vec2d(73, 17), vec2Of(0, 166), texture)

    val production = StaticBarProvider(0.0, Config.windTurbineMaxProduction,
            tile.windTurbineModule.production::storage)

    val openSpace = StaticBarProvider(0.0, 1.0, tile.windTurbineModule::openSpace)
    val wind = StaticBarProvider(0.0, 1.0, tile.windTurbineModule::currentWind)

    +CompVerticalBar(production, 3, Vec2d(89, 17), production.toEnergyText())
    +CompVerticalBar(openSpace, 3, Vec2d(98, 17), openSpace.toPercentText("Wind not blocked: "))
    +CompVerticalBar(wind, 2, Vec2d(107, 17), wind.toPercentText("Wind: ", "%"))
}

fun guiElectricHeater(gui: GuiBase, container: ContainerElectricHeater) = gui.run {
    val tile = container.tile
    val texture = guiTexture("wind_turbine")

    +CompBackground(texture)
    +CompElectricBar(tile.electricNode, Vec2d(64, 17))
    +CompStorageBar(tile.storageModule, Vec2d(73, 17), vec2Of(0, 166), texture)

    val limit = Config.electricHeaterMaxProduction

    val consumption = StaticBarProvider(0.0, limit, tile.electricHeaterModule.consumption::storage)
    val production = StaticBarProvider(0.0, limit, tile.electricHeaterModule.production::storage)

    +CompVerticalBar(consumption, 3, Vec2d(89, 17), consumption.toEnergyText())
    +CompVerticalBar(production, 6, Vec2d(98, 17), production.toHeatPerTickText())
    +CompHeatBar(tile.heatNode, Vec2d(107, 17))
}

fun guiRfHeater(gui: GuiBase, container: ContainerRfHeater) = gui.run {
    val tile = container.tile
    val texture = guiTexture("rf_heater")

    +CompBackground(texture)
    +CompRfBar(tile.storage, Vec2d(78, 17))

    val limit = Config.electricHeaterMaxProduction

    val consumption = StaticBarProvider(0.0, limit, tile.electricHeaterModule.consumption::storage)
    val production = StaticBarProvider(0.0, limit, tile.electricHeaterModule.production::storage)

    +CompVerticalBar(consumption, 3, Vec2d(89, 17), consumption.toIntText(postfix = " RF/t"))
    +CompVerticalBar(production, 6, Vec2d(98, 17), production.toHeatPerTickText())
    +CompHeatBar(tile.node, Vec2d(107, 17))
}
