package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.CallbackBarProvider
import com.cout970.magneticraft.gui.client.components.CompBackground
import com.cout970.magneticraft.gui.client.components.CompElectricBar
import com.cout970.magneticraft.gui.client.components.CompVerticalBar
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.tileentity.electric.TileElectricFurnace
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 22/07/2016.
 */
class GuiElectricFurnace(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileElectricFurnace)

    override fun initComponents() {
        components.add(CompBackground("electric_furnace"))
        components.add(CompElectricBar(tile.mainNode, Vec2d(58, 64) + box.start))
        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.production.storage.toDouble() }, { Config.electricFurnaceMaxConsumption.toDouble() }, { 0.0 }),
                3, Vec2d(69, 64) + box.start, { listOf(String.format("%.2fW", tile.production.storage)) }))

        val callback = CallbackBarProvider({ tile.burningTime.toDouble() }, { TileElectricFurnace.MAX_BURNING_TIME.toDouble() }, { 0.0 })
        components.add(CompVerticalBar(callback, 2, Vec2d(80, 64) + box.start, { listOf("Burning: " + "%.1f".format(callback.getLevel() * 100) + "%") }))
    }
}