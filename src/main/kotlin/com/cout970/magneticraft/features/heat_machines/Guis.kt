package com.cout970.magneticraft.features.heat_machines

import com.cout970.magneticraft.features.multiblocks.ContainerBigCombustionChamber
import com.cout970.magneticraft.misc.guiTexture
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.misc.vector.vec2Of
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.gui.components.CompBackground
import com.cout970.magneticraft.systems.gui.components.bars.*
import com.cout970.magneticraft.systems.gui.render.GuiBase
import com.cout970.magneticraft.systems.gui.render.TankIO
import com.cout970.magneticraft.systems.gui.render.dsl

/**
 * Created by cout970 on 2017/08/10.
 */

fun guiCombustionChamber(gui: GuiBase, container: ContainerCombustionChamber) = gui.run {
    val tile = container.tile

    +CompBackground(guiTexture("combustion_chamber"))

    val burningCallback = CallbackBarProvider(
        { tile.combustionChamberModule.maxBurningTime - tile.combustionChamberModule.burningTime.toDouble() },
        tile.combustionChamberModule::maxBurningTime, { 0.0 })

    +CompVerticalBar(burningCallback, 1, Vec2d(69, 16), burningCallback.toPercentText("Fuel: "))
    +CompHeatBar(tile.node, Vec2d(80, 16))
}

fun guiSteamBoiler(gui: GuiBase, container: ContainerSteamBoiler) = gui.run {
    val tile = container.tile
    val texture = guiTexture("steam_boiler")

    +CompBackground(texture)

    val production = tile.boilerModule.production.toBarProvider(tile.boilerModule.maxProduction)

    +CompHeatBar(tile.node, Vec2d(58, 16))
    +CompVerticalBar(production, 3, Vec2d(69, 16), production.toFluidPerTick())

    +CompFluidBar(vec2Of(80, 16), texture, vec2Of(0, 166), tile.waterTank)
    +CompFluidBar(vec2Of(102, 16), texture, vec2Of(0, 166), tile.steamTank)
}

fun guiGasificationUnit(gui: GuiBase, container: ContainerGasificationUnit) = gui.dsl {
    val tile = container.tile

    bars {
        heatBar(tile.heatNode)
        electricConsumption(tile.process.consumption, Config.gasificationUnitConsumption)
        progressBar(tile.process.timedProcess)
        slotPair()
        tank(tile.tank, TankIO.OUT)
    }
}

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

fun guiBrickFurnace(gui: GuiBase, container: ContainerBrickFurnace) = gui.dsl {
    val tile = container.tile

    bars {
        heatBar(tile.node)
        electricConsumption(tile.processModule.consumption, Config.electricFurnaceMaxConsumption)
        progressBar(tile.processModule.timedProcess)
        slotPair()
    }
}