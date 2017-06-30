package com.cout970.magneticraft.gui.client

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompElectricBar
import com.cout970.magneticraft.gui.client.components.MISC_TEXTURES
import com.cout970.magneticraft.gui.client.components.TransferBar
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.client.core.IComponent
import com.cout970.magneticraft.gui.client.core.IGui
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.modules.ModuleInternalStorage
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 2017/06/12.
 */

class GuiTileBox(container: ContainerBase) : GuiBase(container) {

    override fun initComponents() {
        components.add(CompBackground("box"))
    }
}

class GuiTileBattery(container: ContainerBase) : GuiBase(container) {

    val tile = container.tileEntity as TileBattery

    override fun initComponents() {
        components.add(CompBackground("battery"))
        components.add(CompElectricBar(tile.node, Vec2d(47, 64) + box.start))

        components.add(BatteryBar())

        components.add(TransferBar(
                value = { tile.storageModule.chargeRate.storage.toDouble() },
                min = { -ModuleInternalStorage.MAX_CHARGE_SPEED.toDouble() },
                base = { 0.0 },
                max = { ModuleInternalStorage.MAX_CHARGE_SPEED.toDouble() }, pos = Vec2d(58, 64) + box.start)
        )

        components.add(TransferBar(
                { tile.storageModule.chargeRate.storage.toDouble() },
                { -Config.blockBatteryTransferRate.toDouble() },
                { 0.0 },
                { Config.blockBatteryTransferRate.toDouble() }, Vec2d(58 + 33, 64) + box.start)
        )
    }

    inner class BatteryBar : IComponent {

        val parent = this@GuiTileBattery
        override val box: Box = Box(Vec2d(69, 16) + parent.box.start, Vec2d(16, 48))
        override lateinit var gui: IGui

        override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
            gui.run {
                bindTexture(MISC_TEXTURES)
                val level = (parent.tile.storageModule.energy * 48 / Config.blockBatteryCapacity).toInt()
                drawScaledTexture(this@BatteryBar.box.pos.copy(y = this@BatteryBar.box.pos.y + 48 - level.toDouble()),
                        this@BatteryBar.box.size.copy(y = level.toDouble()), Vec2d.ZERO + Vec2d(0, 48 - level),
                        Vec2d(64, 64))
            }
        }

        override fun drawSecondLayer(mouse: Vec2d) {
            if (mouse in box) {
                gui.drawHoveringText(listOf("${parent.tile.storageModule.energy}J"), mouse)
            }
        }
    }
}

