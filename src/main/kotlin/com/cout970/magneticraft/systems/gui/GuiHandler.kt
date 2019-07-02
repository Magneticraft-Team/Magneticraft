package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.features.automatic_machines.*
import com.cout970.magneticraft.features.computers.*
import com.cout970.magneticraft.features.electric_machines.TileBattery
import com.cout970.magneticraft.features.electric_machines.TileElectricFurnace
import com.cout970.magneticraft.features.electric_machines.TileThermopile
import com.cout970.magneticraft.features.electric_machines.TileWindTurbine
import com.cout970.magneticraft.features.heat_machines.*
import com.cout970.magneticraft.features.manual_machines.ContainerFabricator
import com.cout970.magneticraft.features.manual_machines.TileBox
import com.cout970.magneticraft.features.manual_machines.TileFabricator
import com.cout970.magneticraft.features.manual_machines.guiFabricator
import com.cout970.magneticraft.features.multiblocks.*
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.systems.gui.containers.ContainerBase
import com.cout970.magneticraft.systems.gui.json.*
import com.cout970.magneticraft.systems.gui.render.GuiBase
import com.cout970.magneticraft.systems.manual.ContainerGuideBook
import com.cout970.magneticraft.systems.manual.GuiGuideBook
import com.cout970.magneticraft.systems.tilemodules.ModuleShelvingUnitMb
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * This class handles which GUI should be opened when a block or item calls player.openGui(...)
 */
object GuiHandler : IGuiHandler {

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val container = getServerGuiElement(ID, player, world, x, y, z) as ContainerBase
        if (ID == -2) {
            return GuiGuideBook(container as ContainerGuideBook)
        }

        // @formatter:off
        return when (container) {
            is JsonContainer                 -> JsonGui(container)
//            is ContainerBox                  -> guiOf(container, ::guiBox)
//            is ContainerElectricHeater       -> guiOf(container, ::guiElectricHeater)
//            is ContainerCombustionChamber    -> guiOf(container, ::guiCombustionChamber)
//            is ContainerSteamBoiler          -> guiOf(container, ::guiSteamBoiler)
//            is ContainerBattery              -> guiOf(container, ::guiBattery)
//            is ContainerElectricFurnace      -> guiOf(container, ::guiElectricFurnace)
//            is ContainerBrickFurnace         -> guiOf(container, ::guiBrickFurnace)
//            is ContainerThermopile           -> guiOf(container, ::guiThermopile)
//            is ContainerWindTurbine          -> guiOf(container, ::guiWindTurbine)
//            is ContainerRfHeater             -> guiOf(container, ::guiRfHeater)
//            is ContainerGasificationUnit     -> guiOf(container, ::guiGasificationUnit)
            is ContainerGrinder              -> guiOf(container, ::guiGrinder)
            is ContainerSieve                -> guiOf(container, ::guiSieve)
            is ContainerSolarTower           -> guiOf(container, ::guiSolarTower)
            is ContainerContainer            -> guiOf(container, ::guiContainer)
            is ContainerPumpjack             -> guiOf(container, ::guiPumpjack)
            is ContainerShelvingUnit         -> guiOf(container, ::guiShelvingUnit)
            is ContainerComputer             -> guiOf(container, ::guiComputer)
            is ContainerMiningRobot          -> guiOf(container, ::guiMiningRobot)
            is ContainerHydraulicPress       -> guiOf(container, ::guiHydraulicPress)
            is ContainerOilHeater            -> guiOf(container, ::guiOilHeater)
            is ContainerRefinery             -> guiOf(container, ::guiRefinery)
            is ContainerSteamEngine          -> guiOf(container, ::guiSteamEngine)
            is ContainerBigCombustionChamber -> guiOf(container, ::guiBigCombustionChamber)
            is ContainerInserter             -> guiOf(container, ::guiInserter)
            is ContainerRelay                -> guiOf(container, ::guiRelay)
            is ContainerFilter               -> guiOf(container, ::guiFilter)
            is ContainerFabricator           -> guiOf(container, ::guiFabricator)
            else -> null
        }
        // @formatter:on
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)

        if (ID == -2) {
            return ContainerGuideBook(player, world, pos)
        }

        @Suppress("MoveVariableDeclarationIntoWhen")
        val tile = world.getTileEntity(pos)

        // @formatter:off
        return when (tile) {
            is TileBox                  -> jsonContainer("box", GuiBuilder::boxGui, tile, player, world, pos)
            is TileElectricHeater       -> jsonContainer("electric_heater", GuiBuilder::electricHeaterGui, tile, player, world, pos)
            is TileCombustionChamber    -> jsonContainer("combustion_chamber", GuiBuilder::combustionChamberGui, tile, player, world, pos)
            is TileSteamBoiler          -> jsonContainer("steam_boiler", GuiBuilder::steamBoilerGui, tile, player, world, pos)
            is TileBattery              -> jsonContainer("battery_block", GuiBuilder::batteryBlockGui, tile, player, world, pos)
            is TileElectricFurnace      -> jsonContainer("electric_furnace", GuiBuilder::electricFurnaceGui, tile, player, world, pos)
            is TileBrickFurnace         -> jsonContainer("brick_furnace", GuiBuilder::brickFurnaceGui, tile, player, world, pos)
            is TileThermopile           -> jsonContainer("thermopile", GuiBuilder::thermopileGui, tile, player, world, pos)
            is TileWindTurbine          -> jsonContainer("wind_turbine", GuiBuilder::windTurbineGui, tile, player, world, pos)
            is TileRfHeater             -> jsonContainer("rf_heater", GuiBuilder::rfHeaterGui, tile, player, world, pos)
            is TileGasificationUnit     -> jsonContainer("gasification_unit", GuiBuilder::gasificationUnitGui, tile, player, world, pos)

            is TileShelvingUnit         -> ContainerShelvingUnit(tile, player, world, pos, ModuleShelvingUnitMb.Level.values()[ID])
            is TileComputer             -> ContainerComputer(tile, player, world, pos)
            is TileMiningRobot          -> ContainerMiningRobot(tile, player, world, pos)
            is TileGrinder              -> ContainerGrinder(tile, player, world, pos)
            is TileSieve                -> ContainerSieve(tile, player, world, pos)
            is TileSolarTower           -> ContainerSolarTower(tile, player, world, pos)
            is TileContainer            -> ContainerContainer(tile, player, world, pos)
            is TilePumpjack             -> ContainerPumpjack(tile, player, world, pos)
            is TileHydraulicPress       -> ContainerHydraulicPress(tile, player, world, pos)
            is TileOilHeater            -> ContainerOilHeater(tile, player, world, pos)
            is TileRefinery             -> ContainerRefinery(tile, player, world, pos)

            is TileSteamEngine          -> ContainerSteamEngine(tile, player, world, pos)
            is TileBigCombustionChamber -> ContainerBigCombustionChamber(tile, player, world, pos)
            is TileInserter             -> ContainerInserter(tile, player, world, pos)
            is TileRelay                -> ContainerRelay(tile, player, world, pos)
            is TileFilter               -> ContainerFilter(tile, player, world, pos)
            is TileFabricator           -> ContainerFabricator(tile, player, world, pos)
            else -> null
        }
        // @formatter:on
    }

    private fun <T : ContainerBase> guiOf(container: T, func: (GuiBase, T) -> Unit): GuiBase {
        return object : GuiBase(container) {
            override fun initComponents() {
                func(this, container)
            }
        }
    }
}