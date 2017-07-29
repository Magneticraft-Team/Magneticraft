package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.computer.DeviceMonitor
import com.cout970.magneticraft.computer.Motherboard
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.ContainerComputer
import com.cout970.magneticraft.gui.common.ContainerShelvingUnit
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.formatHeat
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileCombustionChamber
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.util.resource
import com.cout970.magneticraft.util.toKelvinFromCelsius
import com.cout970.magneticraft.util.vector.Vec2d
import com.cout970.magneticraft.util.vector.vec2Of

/**
 * Created by cout970 on 2017/06/12.
 */

class GuiBox(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground("box"))
    }
}

class GuiElectricFurnace(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileElectricFurnace)

    override fun initComponents() {
        components.add(CompBackground("electric_furnace"))
        components.add(CompElectricBar(tile.node, Vec2d(58, 64) + box.start))
        val consumptionCallback = CallbackBarProvider(
                callback = { tile.processModule.production.storage.toDouble() },
                max = { Config.electricFurnaceMaxConsumption },
                min = { 0.0 }
        )
        components.add(CompVerticalBar(consumptionCallback, 3, Vec2d(69, 64) + box.start,
                { listOf(String.format("%.2fW", consumptionCallback.callback())) }))

        val processCallback = CallbackBarProvider(
                { tile.processModule.timedProcess.timer.toDouble() },
                { tile.processModule.timedProcess.limit().toDouble() },
                { 0.0 }
        )
        components.add(CompVerticalBar(processCallback, 2, Vec2d(80, 64) + box.start,
                { listOf("Burning: " + "%.1f".format(processCallback.getLevel() * 100) + "%") }))
    }
}

class GuiBattery(container: ContainerBase) : GuiBase(container) {

    val tile = container.tileEntity as TileBattery

    override fun initComponents() {
        components.add(CompBackground("battery"))
        components.add(CompElectricBar(tile.node, Vec2d(47, 64) + box.start))

        components.add(StorageBar(this, tile.storageModule))

        components.add(TransferBar(
                value = { tile.storageModule.chargeRate.storage.toDouble() },
                min = { -tile.storageModule.maxChargeSpeed },
                base = { 0.0 },
                max = { tile.storageModule.maxChargeSpeed }, pos = Vec2d(58, 64) + box.start)
        )

        components.add(TransferBar(
                { tile.itemChargeModule.itemChargeRate.storage.toDouble() },
                { -tile.itemChargeModule.transferRate.toDouble() },
                { 0.0 },
                { tile.itemChargeModule.transferRate.toDouble() }, Vec2d(58 + 33, 64) + box.start)
        )
    }
}

class GuiShelvingUnit(container: ContainerBase) : GuiBase(container) {

    companion object {
        @JvmStatic
        val TEXTURE = resource("textures/gui/shelving_unit.png")
    }

    override fun initComponents() {
        xSize = 194
        ySize = 207
        components.add(CompBackground("shelving_unit", size = vec2Of(194, 207)))
        val scrollBar = CompScrollBar(vec2Of(174, 21), TEXTURE)
        components.add(scrollBar)
//        components.add(CompButton())
        components.add(CompShelvingUnit(container as ContainerShelvingUnit, scrollBar))
    }
}

class GuiComputer(container: ContainerBase) : GuiBase(container) {

    val monitor: DeviceMonitor = (container as ContainerComputer).monitor
    val motherboard: Motherboard = (container as ContainerComputer).motherboard

    init {
        xSize = 350
        ySize = 255
    }

    override fun initComponents() {
        val listener = object : IButtonListener {
            override fun onPress(button: AbstractButton, mouse: Vec2d, mouseButton: Int): Boolean {
                val ibd = IBD().apply { setInteger(50, button.ID) }
                container.sendUpdate(ibd)
                return true
            }
        }
        components.add(CompBackground("old_monitor", textureSize = Vec2d(512, 512), size = Vec2d(350, 255)))
        components.add(MonitorComponent(monitor))
        components.add(SimpleButton(0, Vec2d(23, 220), listener, Vec2d(0, 49)))
        components.add(SimpleButton(1, Vec2d(33, 220), listener, Vec2d(0, 49)))
        components.add(SimpleButton(2, Vec2d(43, 220), listener, Vec2d(0, 49)))
        components.add(CompGreenLight(box.start + Vec2d(14, 221), { motherboard.isOnline() }))
    }
}

class GuiCombustionChamber(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileCombustionChamber)

    override fun initComponents() {
        components.add(CompBackground("combustion_chamber"))
        val burningCallback = CallbackBarProvider(
                { tile.combustionChamberModule.maxBurningTime.toDouble() - tile.combustionChamberModule.burningTime.toDouble() },
                { tile.combustionChamberModule.maxBurningTime.toDouble() },
                { 0.0 }
        )
        components.add(CompVerticalBar(burningCallback, 1, Vec2d(69, 64) + box.start,
                { listOf(String.format("Fuel: %.1f%%", burningCallback.getLevel() * 100)) }))

        val heatCallback = CallbackBarProvider(
                { tile.combustionChamberModule.heat.toDouble() },
                { 99.5.toKelvinFromCelsius() },
                { 24.0.toKelvinFromCelsius() }
        )
        components.add(CompVerticalBar(heatCallback, 2, Vec2d(80, 64) + box.start,
                { listOf("Heat: " + formatHeat(heatCallback.callback())) }))
    }
}


