package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.bars.CompElectricBar
import com.cout970.magneticraft.gui.client.components.bars.BatteryStorageBar
import com.cout970.magneticraft.gui.client.components.bars.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.bars.CompVerticalBar
import com.cout970.magneticraft.gui.client.components.bars.TransferRateBar
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.util.guiTexture
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/08/10.
 */

class GuiElectricFurnace(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileElectricFurnace)

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
        components.add(
                CompVerticalBar(processCallback, 2, Vec2d(80, 16),
                        { listOf("Burning: " + "%.1f".format(processCallback.getLevel() * 100) + "%") }))
    }
}

class GuiBattery(container: ContainerBase) : GuiBase(container) {

    val tile = container.tileEntity as TileBattery

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