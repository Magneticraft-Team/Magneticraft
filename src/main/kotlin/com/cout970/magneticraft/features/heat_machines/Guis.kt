package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.features.multiblocks.ContainerBigCombustionChamber
import com.cout970.magneticraft.systems.gui.components.bars.CallbackBarProvider
import com.cout970.magneticraft.systems.gui.components.bars.toPercentText
import com.cout970.magneticraft.systems.gui.render.GuiBase
import com.cout970.magneticraft.systems.gui.render.TankIO
import com.cout970.magneticraft.systems.gui.render.dsl

/**
 * Created by cout970 on 2017/08/10.
 */


fun guiBigCombustionChamber(gui: GuiBase, container: ContainerBigCombustionChamber) = gui.dsl {
    val tile = container.tile

    val burningCallback = CallbackBarProvider(
        { tile.bigCombustionChamberModule.maxBurningTime - tile.bigCombustionChamberModule.burningTime.toDouble() },
        tile.bigCombustionChamberModule::maxBurningTime, { 0.0 })

    bars {
        heatBar(tile.node)
        genericBar(1, 6, burningCallback, burningCallback.toPercentText("Fuel: "))
        tank(tile.tank, TankIO.IN)
        singleSlot()
    }
}