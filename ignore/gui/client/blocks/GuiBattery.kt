package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.IComponent
import com.cout970.magneticraft.gui.client.IGui
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompElectricBar
import com.cout970.magneticraft.gui.client.components.MISC_TEXTURES
import com.cout970.magneticraft.gui.client.components.TransferBar
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.misc.gui.Box
import com.cout970.magneticraft.tileentity.electric.TileBattery
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 11/07/2016.
 */
class GuiBattery(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileBattery)

    override fun initComponents() {
        components.add(CompBackground("battery"))

        components.add(CompElectricBar(tile.mainNode, Vec2d(47, 64) + box.start))

        components.add(BatterBar())

        components.add(TransferBar({ tile.chargeRate.storage.toDouble() },
                { -TileBattery.MAX_CHARGE_SPEED.toDouble() }, { 0.0 }, { TileBattery.MAX_CHARGE_SPEED.toDouble() }, Vec2d(58, 64) + box.start))

        components.add(TransferBar({ tile.itemChargeRate.storage.toDouble() },
                { -Config.blockBatteryTransferRate.toDouble() }, { 0.0 }, { Config.blockBatteryTransferRate.toDouble() }, Vec2d(58 + 33, 64) + box.start))

    }

    inner class BatterBar : IComponent {
        override val box: Box = Box(Vec2d(69, 16) + this@GuiBattery.box.start, Vec2d(16, 48))
        override lateinit var gui: IGui

        override fun drawFirstLayer(mouse: Vec2d, partialTicks: Float) {
            gui.run {
                bindTexture(MISC_TEXTURES)
                val level = (this@GuiBattery.tile.storage * 48 / Config.blockBatteryCapacity).toInt()
                drawScaledTexture(this@BatterBar.box.pos.copy(y = this@BatterBar.box.pos.y + 48 - level.toDouble()),
                        this@BatterBar.box.size.copy(y = level.toDouble()), Vec2d.ZERO + Vec2d(0, 48 - level), Vec2d(64, 64))
            }
        }

        override fun drawSecondLayer(mouse: Vec2d) {
            if (mouse in box) {
                gui.drawHoveringText(listOf("${this@GuiBattery.tile.storage}J"), mouse)
            }
        }
    }
}