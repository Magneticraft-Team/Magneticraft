package com.cout970.magneticraft.gui.client.blocks

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.gui.client.GuiBase
import com.cout970.magneticraft.gui.client.components.*
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import com.cout970.magneticraft.util.vector.Vec2d

/**
 * Created by cout970 on 11/07/2016.
 */
class GuiGrinder(container: ContainerBase) : GuiBase(container) {

    val tile = (container.tileEntity as TileGrinder)

    override fun initComponents() {
        components.add(CompBackground("battery"))

        components.add(CompElectricBar(tile.node, Vec2d(47, 64) + box.start))

        components.add(CompGreenLight(Vec2d(57, 58) + box.start, { tile.redPower > 0 }))

        components.add(CompVerticalBar(
                CallbackBarProvider({ tile.production.storage.toDouble() }, { Config.grinderConsumption * 2 }, { 0.0 }),
                3, Vec2d(69, 64) + box.start, { listOf(String.format("%.2fW", tile.production.storage)) }))

        val callback = CallbackBarProvider({ tile.craftingProcess.timer.toDouble() },
                { if (tile.getRecipe() != null) tile.getRecipe()!!.duration.toDouble() else 100.0 }, { 0.0 })
        components.add(CompVerticalBar(callback, 2, Vec2d(80, 64) + box.start, { listOf("Progress: " + "%.1f".format(callback.getLevel() * 100) + "%") }))
    }
}