package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.client.components.bars.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.bars.CompElectricBar
import com.cout970.magneticraft.gui.client.components.bars.CompFluidBar
import com.cout970.magneticraft.gui.client.components.bars.CompVerticalBar
import com.cout970.magneticraft.gui.client.components.buttons.AbstractButton
import com.cout970.magneticraft.gui.client.components.buttons.ButtonState
import com.cout970.magneticraft.gui.client.components.buttons.MultiButton
import com.cout970.magneticraft.gui.client.components.buttons.SimpleButton
import com.cout970.magneticraft.gui.client.core.DrawableBox
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_FILTER
import com.cout970.magneticraft.gui.common.core.DATA_ID_SHELVING_UNIT_LEVEL
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.modules.ModulePumpjack.Status.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of
import org.lwjgl.input.Keyboard

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiShelvingUnit(container: ContainerBase) : GuiBase(container) {

    lateinit var textInput: CompTextInput

    override fun initComponents() {
        xSize = 194
        ySize = 207
        val texture = guiTexture("shelving_unit")
        val scrollBar = CompScrollBar(vec2Of(174, 21), texture = texture)

        textInput = CompTextInput(fontRenderer, vec2Of(10, 7), vec2Of(86, 13)).apply { isFocused = true }
        +CompBackground(texture, size = vec2Of(194, 207))

        val button1Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 75) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 75) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 0) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 0) to vec2Of(23, 24))
        )
        val button2Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 100) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 100) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 25) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 25) to vec2Of(23, 24))
        )
        val button3Map = mapOf(
                ButtonState.UNPRESSED to (vec2Of(194, 125) to vec2Of(23, 24)),
                ButtonState.HOVER_UNPRESSED to (vec2Of(194, 125) to vec2Of(23, 24)),
                ButtonState.PRESSED to (vec2Of(194, 50) to vec2Of(23, 24)),
                ButtonState.HOVER_PRESSED to (vec2Of(194, 50) to vec2Of(23, 24))
        )
        val buttons = listOf(
                MultiButton(0, texture, vec2Of(176, 129) to vec2Of(23, 24), vec2Of(256), { button1Map[it]!! }),
                MultiButton(1, texture, vec2Of(176, 154) to vec2Of(23, 24), vec2Of(256), { button2Map[it]!! }),
                MultiButton(2, texture, vec2Of(176, 179) to vec2Of(23, 24), vec2Of(256), { button3Map[it]!! })
        )
        +scrollBar
        +textInput
        buttons.forEach { +it; it.listener = this::onPress; it.allButtons = buttons }
        +CompShelvingUnit(container as ContainerShelvingUnit, scrollBar, textInput)

        (container as? ContainerShelvingUnit)?.let {
            buttons[2 - it.level.levelIndex].state = ButtonState.PRESSED
        }
        Keyboard.enableRepeatEvents(true)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
        val ibd = IBD().apply {
            setInteger(DATA_ID_SHELVING_UNIT_LEVEL, button.id)
            setString(DATA_ID_SHELVING_UNIT_FILTER, "")
        }
        (container as ContainerShelvingUnit)
        textInput.text = ""
        container.filterSlots("")
        container.sendUpdate(ibd)
        container.switchLevel(ModuleShelvingUnitMb.Level.values()[button.id])
        return true
    }

    override fun onGuiClosed() {
        Keyboard.enableRepeatEvents(false)
        super.onGuiClosed()
    }
}

class GuiGrinder(val grinder: ContainerGrinder) : GuiBase(grinder) {

    override fun initComponents() {
        val tile = grinder.tile

        +CompBackground(guiTexture("grinder"))
        +CompElectricBar(tile.node, Vec2d(52, 16))
        val consumptionCallback = CallbackBarProvider(
                callback = { tile.processModule.production.storage.toDouble() },
                max = { Config.grinderMaxConsumption },
                min = { 0.0 }
        )
        +CompVerticalBar(consumptionCallback, 3,
                Vec2d(63, 16),
                { listOf(String.format("%.2fW", consumptionCallback.callback())) })

        val processCallback = CallbackBarProvider(
                { tile.processModule.timedProcess.timer.toDouble() },
                { tile.processModule.timedProcess.limit().toDouble() },
                { 0.0 }
        )
        +CompVerticalBar(processCallback, 6, Vec2d(74, 16),
                { listOf("Processing: " + "%.1f".format(processCallback.getLevel() * 100) + "%") })
    }
}

class GuiSieve(val sieve: ContainerSieve) : GuiBase(sieve) {

    override fun initComponents() {
        val tile = sieve.tile

        +CompBackground(guiTexture("sieve"))
        +CompElectricBar(tile.node, Vec2d(41, 16))
        val consumptionCallback = CallbackBarProvider(
                callback = { tile.processModule.production.storage.toDouble() },
                max = { Config.sieveMaxConsumption },
                min = { 0.0 }
        )
        +CompVerticalBar(consumptionCallback, 3,
                Vec2d(52, 16),
                { listOf(String.format("%.2fW", consumptionCallback.callback())) })

        val processCallback = CallbackBarProvider(
                { tile.processModule.timedProcess.timer.toDouble() },
                { tile.processModule.timedProcess.limit().toDouble() },
                { 0.0 }
        )
        +CompVerticalBar(processCallback, 6, Vec2d(63, 16),
                { listOf("Processing: " + "%.1f".format(processCallback.getLevel() * 100) + "%") })
    }
}

class GuiSolarTower(val tower: ContainerSolarTower) : GuiBase(tower) {

    override fun initComponents() {
        val tile = tower.tile
        val texture = guiTexture("solar_tower")

        +CompBackground(texture)

        val prodCallback = CallbackBarProvider(
                { tile.steamBoilerModule.production.storage.toDouble() },
                { tile.steamBoilerModule.maxSteamProduction.toDouble() },
                { 0.0 }
        )

        +CompVerticalBar(prodCallback, 3, Vec2d(53, 16),
                { listOf("Steam production: ${prodCallback.callback()} mB/t") })

        val heatCallback = CallbackBarProvider(
                { tile.solarTowerModule.production.storage.toDouble() },
                { tile.steamBoilerModule.heatCapacity.toDouble() },
                { 0.0 }
        )

        +CompVerticalBar(heatCallback, 2, Vec2d(42, 16),
                { listOf("Heat received: ${heatCallback.callback()} Heat/t") })

        +CompFluidBar(vec2Of(64, 16), texture, vec2Of(0, 166), tile.waterTank)
        +CompFluidBar(vec2Of(86, 16), texture, vec2Of(0, 166), tile.steamTank)

        val buttonSize = vec2Of(16)
        val buttonMap = mapOf(
                ButtonState.UNPRESSED to (vec2Of(16, 166) to buttonSize),
                ButtonState.HOVER_UNPRESSED to (vec2Of(16, 182) to buttonSize),
                ButtonState.HOVER_PRESSED to (vec2Of(16, 198) to buttonSize)
        )

        +SimpleButton(
                id = 0,
                box = vec2Of(108, 48) to buttonSize,
                texture = texture,
                textureSize = vec2Of(256, 256),
                uvGetter = buttonMap::getValue
        ).apply { listener = tower::onClick }
    }
}

class GuiContainer(val inv: ContainerContainer) : GuiBase(inv) {

    override fun initComponents() {
        +CompBackground(guiTexture("container"))

        val mod = inv.tile.stackInventoryModule

        val callback = CallbackBarProvider(
                { mod.amount.toDouble() },
                { mod.maxItems.toDouble() },
                { 0.0 }
        )

        +CompVerticalBar(callback, 7, Vec2d(74, 16),
                { listOf("Items: ${mod.amount}/${mod.maxItems}") })
    }
}

class GuiPumpjack(val cont: ContainerPumpjack) : GuiBase(cont) {

    override fun initComponents() {
        val tile = cont.tile
        val mod = tile.pumpjackModule
        val texture = guiTexture("pumpjack")

        +CompBackground(texture)

        +CompElectricBar(tile.node, Vec2d(53, 16))
        val consumptionCallback = CallbackBarProvider(
                callback = { mod.production.storage.toDouble() },
                max = { Config.pumpjackConsumption },
                min = { 0.0 }
        )
        +CompVerticalBar(consumptionCallback, 3,
                Vec2d(64, 16),
                { listOf("%.2fW".format(consumptionCallback.callback())) })


        val processCallback = CallbackBarProvider(
                {
                    when (mod.status) {
                        SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> mod.processPercent.toDouble()
                        SEARCHING_SOURCE, EXTRACTING -> mod.depositLeft.toDouble()
                    }
                },
                {
                    when (mod.status) {
                        SEARCHING_OIL, SEARCHING_DEPOSIT, DIGGING -> 1.0
                        SEARCHING_SOURCE, EXTRACTING -> mod.depositSize.toDouble()
                    }
                },
                { 0.0 }
        )

        +CompVerticalBar(processCallback, 6, Vec2d(75, 16)) {
            when (mod.status) {
                SEARCHING_OIL -> listOf("Searching for oil: ${"%.2f".format(mod.processPercent * 100)}%")
                SEARCHING_DEPOSIT -> listOf("Scanning oil deposit: ${"%.2f".format(mod.processPercent * 100)}%")
                DIGGING -> listOf("Mining to the oil deposit: ${"%.2f".format(mod.processPercent * 100)}%")
                SEARCHING_SOURCE, EXTRACTING -> listOf("Oil deposit: ${mod.depositLeft}/${mod.depositSize} blocks")
            }
        }

        +CompFluidBar(vec2Of(86, 16), texture, vec2Of(0, 166), tile.tank)

        val size = vec2Of(16, 16)
        val box = pos + Vec2d(108, 16) to size

        +CompLight(
                on = DrawableBox(box, vec2Of(26, 166) to size, vec2Of(256)),
                off = DrawableBox(box, vec2Of(26, 9999) to size, vec2Of(256)),
                texture = texture, condition = { mod.status == SEARCHING_OIL }
        )

        +CompLight(
                on = DrawableBox(box, vec2Of(26, 166 + 16) to size, vec2Of(256)),
                off = DrawableBox(box, vec2Of(26, 9999) to size, vec2Of(256)),
                texture = texture, condition = { mod.status == SEARCHING_DEPOSIT }
        )

        +CompLight(
                on = DrawableBox(box, vec2Of(26, 166 + 16 * 2) to size, vec2Of(256)),
                off = DrawableBox(box, vec2Of(26, 9999) to size, vec2Of(256)),
                texture = texture, condition = { mod.status == DIGGING }
        )

        +CompLight(
                on = DrawableBox(box, vec2Of(26, 166 + 16 * 3) to size, vec2Of(256)),
                off = DrawableBox(box, vec2Of(26, 9999) to size, vec2Of(256)),
                texture = texture, condition = { mod.status == EXTRACTING }
        )

        +CompLight(
                on = DrawableBox(box, vec2Of(26, 166 + 16 * 4) to size, vec2Of(256)),
                off = DrawableBox(box, vec2Of(26, 9999) to size, vec2Of(256)),
                texture = texture, condition = { mod.status == SEARCHING_SOURCE }
        )
    }
}