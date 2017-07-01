package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileElectricFurnace
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/06/12.
 */

class GuiTileBox(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground("box"))
    }
}

class GuiTileElectricFurnace(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileElectricFurnace)

    override fun initComponents() {
        components.add(CompBackground("electric_furnace"))
        components.add(CompElectricBar(tile.node, Vec2d(58, 64) + box.start))
        val storageCallback = CallbackBarProvider(
                callback = { tile.processModule.production.storage.toDouble() },
                max = { Config.electricFurnaceMaxConsumption },
                min = { 0.0 }
        )
        components.add(CompVerticalBar(storageCallback, 3, Vec2d(69, 64) + box.start,
                { listOf(String.format("%.2fW", storageCallback.callback())) }))

        val processCallback = CallbackBarProvider(
                { tile.processModule.timedProcess.timer.toDouble() },
                { tile.processModule.timedProcess.limit().toDouble() },
                { 0.0 }
        )
        components.add(CompVerticalBar(processCallback, 2, Vec2d(80, 64) + box.start,
                { listOf("Burning: " + "%.1f".format(processCallback.getLevel() * 100) + "%") }))
    }
}

class GuiTileBattery(container: ContainerBase) : GuiBase(container) {

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

//        components.add(TransferBar(
//                { tile.storageModule.chargeRate.storage.toDouble() },
//                { -Config.blockBatteryTransferRate.toDouble() },
//                { 0.0 },
//                { Config.blockBatteryTransferRate.toDouble() }, Vec2d(58 + 33, 64) + box.start)
//        )
    }
}

