package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.client.components.bars.*
import com.cout970.magneticraft.gui.client.components.buttons.AbstractButton
import com.cout970.magneticraft.gui.client.components.buttons.MultiButton
import com.cout970.magneticraft.gui.client.components.buttons.buttonOf
import com.cout970.magneticraft.gui.client.components.buttons.buttonUV
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_FILTER
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_LEVEL
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.modules.ModulePumpjack
import com.cout970.magneticraft.tileentity.modules.ModulePumpjack.Status.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiShelvingUnit(gui: GuiBase, container: ContainerShelvingUnit) = gui.run {
    var scrollBar: CompScrollBar? = null
    var textInput: CompTextInput? = null
    val texture = guiTexture("shelving_unit")
    val tile = container.tile

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
            MultiButton(0, box = vec2Of(176, 129) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 0), vec2Of(23, 24))),
            MultiButton(1, box = vec2Of(176, 154) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 24 * 3), vec2Of(23, 24))),
            MultiButton(2, box = vec2Of(176, 179) to vec2Of(23, 24), uv = buttonUV(vec2Of(194, 24 * 6), vec2Of(23, 24)))
    )

    buttons.forEach { +it; it.listener = ::onPress; it.allButtons = buttons }
}

fun guiGrinder(gui: GuiBase, container: ContainerGrinder) = gui.run {
    val tile = container.tile
    +CompBackground(guiTexture("grinder"))
    +CompElectricBar(tile.node, Vec2d(52, 16))

    val consumption = tile.processModule.consumption.toBarProvider(Config.grinderMaxConsumption)
    val process = tile.processModule.timedProcess.toBarProvider()

    +CompVerticalBar(consumption, 3, Vec2d(63, 16), consumption.toEnergyText())
    +CompVerticalBar(process, 6, Vec2d(74, 16), process.toPercentText("Processing: "))
}

fun guiSieve(gui: GuiBase, container: ContainerSieve) = gui.run {
    val tile = container.tile

    +CompBackground(guiTexture("sieve"))
    +CompElectricBar(tile.node, Vec2d(41, 16))

    val consumption = tile.processModule.consumption.toBarProvider(Config.sieveMaxConsumption)
    val process = tile.processModule.timedProcess.toBarProvider()

    +CompVerticalBar(consumption, 3, Vec2d(52, 16), consumption.toEnergyText())
    +CompVerticalBar(process, 6, Vec2d(63, 16), process.toPercentText("Processing: "))
}

fun guiSolarTower(gui: GuiBase, container: ContainerSolarTower) = gui.run {
    val tile = container.tile

    val texture = guiTexture("solar_tower")

    +CompBackground(texture)

    +buttonOf(pos = vec2Of(108, 48), uv = vec2Of(16, 166), listener = container::onClick)

    val prod = tile.steamBoilerModule.production.toBarProvider(tile.steamBoilerModule.maxSteamProduction)
    val heat = tile.solarTowerModule.production.toBarProvider(tile.steamBoilerModule.heatCapacity)

    +CompVerticalBar(prod, 3, Vec2d(53, 16), prod.toIntText("Steam consumption: ", "mB/t"))
    +CompVerticalBar(heat, 2, Vec2d(42, 16), heat.toIntText("Heat received: ", " Heat/t"))

    +CompFluidBar(vec2Of(64, 16), texture, vec2Of(0, 166), tile.waterTank)
    +CompFluidBar(vec2Of(86, 16), texture, vec2Of(0, 166), tile.steamTank)
}

fun guiContainer(gui: GuiBase, container: ContainerContainer) = gui.run {
    val mod = container.tile.stackInventoryModule
    val callback = CallbackBarProvider(mod::amount, mod::maxItems, ZERO)

    +CompBackground(guiTexture("container"))
    +CompVerticalBar(callback, 7, Vec2d(74, 16), { listOf("Items: ${mod.amount}/${mod.maxItems}") })
}

fun guiPumpjack(gui: GuiBase, container: ContainerPumpjack) = gui.run {
    val tile = container.tile

    val mod = tile.pumpjackModule
    val texture = guiTexture("pumpjack")

    +CompBackground(texture)
    +CompElectricBar(tile.node, Vec2d(53, 16))

    val consumptionCallback = mod.production.toBarProvider(Config.pumpjackConsumption)

    +CompVerticalBar(consumptionCallback, 3, Vec2d(64, 16), consumptionCallback.toEnergyText())

    val processCallback = CallbackBarProvider({
        when (mod.status) {
            SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> mod.processPercent
            SEARCHING_SOURCE, EXTRACTING -> mod.depositLeft
        }
    }, {
        when (mod.status) {
            SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> 1.0
            SEARCHING_SOURCE, EXTRACTING -> mod.depositSize
        }
    }, ZERO)

    +CompVerticalBar(processCallback, 6, Vec2d(75, 16)) {

        val percent = "%.2f".format(mod.processPercent * 100)
        val amount = "${mod.depositLeft}/${mod.depositSize}"

        when (mod.status) {
            SEARCHING_OIL -> listOf("Searching for oil: $percent%")
            SEARCHING_DEPOSIT -> listOf("Scanning oil deposit: $percent%")
            DIGGING -> listOf("Mining to the oil deposit: $percent%")
            SEARCHING_SOURCE -> listOf("Oil deposit: $amount blocks", "Scanning: $percent%")
            EXTRACTING -> listOf("Oil deposit: $amount blocks", "Extracting...")
        }
    }

    +CompFluidBar(vec2Of(86, 16), texture, vec2Of(0, 166), tile.tank)

    val size = vec2Of(16, 16)
    val pos = pos + Vec2d(108, 16)

    repeat(5) {
        +CompLight(
                on = DrawableBox(pos, size, vec2Of(26, 166 + 16 * it)),
                off = DrawableBox(pos, size, vec2Of(26, 9999)),
                texture = texture, condition = { mod.status == ModulePumpjack.Status.values()[it] }
        )
    }
}