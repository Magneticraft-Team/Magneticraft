package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.features.automatic_machines.TileFilter
import com.cout970.magneticraft.features.automatic_machines.TileInserter
import com.cout970.magneticraft.features.automatic_machines.TileRelay
import com.cout970.magneticraft.features.automatic_machines.TileTransposer
import com.cout970.magneticraft.features.computers.TileComputer
import com.cout970.magneticraft.features.computers.TileMiningRobot
import com.cout970.magneticraft.features.electric_conductors.TileTeslaTower
import com.cout970.magneticraft.features.electric_machines.*
import com.cout970.magneticraft.features.fluid_machines.TileSmallTank
import com.cout970.magneticraft.features.heat_machines.*
import com.cout970.magneticraft.features.manual_machines.TileBox
import com.cout970.magneticraft.features.manual_machines.TileFabricator
import com.cout970.magneticraft.features.multiblocks.tileentities.*
import com.cout970.magneticraft.systems.manual.ContainerGuideBook
import com.cout970.magneticraft.systems.manual.GuiGuideBook
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
            is AutoContainer                 -> AutoGui(container)
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
            is TileBox                  -> autoContainer("box", GuiBuilder::boxGui, tile, player, world, pos)
            is TileElectricHeater       -> autoContainer("electric_heater", GuiBuilder::electricHeaterGui, tile, player, world, pos)
            is TileCombustionChamber    -> autoContainer("combustion_chamber", GuiBuilder::combustionChamberGui, tile, player, world, pos)
            is TileSteamBoiler          -> autoContainer("steam_boiler", GuiBuilder::steamBoilerGui, tile, player, world, pos)
            is TileBattery              -> autoContainer("battery_block", GuiBuilder::batteryBlockGui, tile, player, world, pos)
            is TileElectricFurnace      -> autoContainer("electric_furnace", GuiBuilder::electricFurnaceGui, tile, player, world, pos)
            is TileBrickFurnace         -> autoContainer("brick_furnace", GuiBuilder::brickFurnaceGui, tile, player, world, pos)
            is TileThermopile           -> autoContainer("thermopile", GuiBuilder::thermopileGui, tile, player, world, pos)
            is TileWindTurbine          -> autoContainer("wind_turbine", GuiBuilder::windTurbineGui, tile, player, world, pos)
            is TileRfHeater             -> autoContainer("rf_heater", GuiBuilder::rfHeaterGui, tile, player, world, pos)
            is TileGasificationUnit     -> autoContainer("gasification_unit", GuiBuilder::gasificationUnitGui, tile, player, world, pos)
            is TileGrinder              -> autoContainer("grinder", GuiBuilder::grinderGui, tile, player, world, pos)
            is TileSieve                -> autoContainer("sieve", GuiBuilder::sieveGui, tile, player, world, pos)
            is TileContainer            -> autoContainer("container", GuiBuilder::containerGui, tile, player, world, pos)
            is TilePumpjack             -> autoContainer("pumpjack", GuiBuilder::pumpjackGui, tile, player, world, pos)
            is TileOilHeater            -> autoContainer("oil_heater", GuiBuilder::oilHeaterGui, tile, player, world, pos)
            is TileRefinery             -> autoContainer("refinery", GuiBuilder::refineryGui, tile, player, world, pos)
            is TileSteamEngine          -> autoContainer("steam_engine", GuiBuilder::steamEngineGui, tile, player, world, pos)
            is TileBigCombustionChamber -> autoContainer("big_combustion_chamber", GuiBuilder::bigCombustionChamberGui, tile, player, world, pos)
            is TileBigSteamBoiler       -> autoContainer("big_steam_boiler", GuiBuilder::bigSteamBoilerGui, tile, player, world, pos)
            is TileSteamTurbine         -> autoContainer("steam_turbine", GuiBuilder::steamTurbineGui, tile, player, world, pos)
            is TileRelay                -> autoContainer("relay", GuiBuilder::relayGui, tile, player, world, pos)
            is TileFilter               -> autoContainer("filter", GuiBuilder::filterGui, tile, player, world, pos)
            is TileFabricator           -> autoContainer("fabricator", GuiBuilder::fabricatorGui, tile, player, world, pos)
            is TileSolarTower           -> autoContainer("solar_tower", GuiBuilder::solarTowerGui, tile, player, world, pos)
            is TileInserter             -> autoContainer("inserter", GuiBuilder::inserterGui, tile, player, world, pos)
            is TileHydraulicPress       -> autoContainer("hydraulic_press", GuiBuilder::hydraulicPressGui, tile, player, world, pos)
            is TileShelvingUnit         -> autoContainer("shelving_unit", GuiBuilder::shelvingUnitGui, tile, player, world, pos)
            is TileRfTransformer        -> autoContainer("rf_transformer", GuiBuilder::rfTransformerGui, tile, player, world, pos)
            is TileElectricEngine       -> autoContainer("electric_engine", GuiBuilder::electricEngineGui, tile, player, world, pos)
            is TileAirLock              -> autoContainer("airlock", GuiBuilder::airlockGui, tile, player, world, pos)
            is TileSmallTank            -> autoContainer("small_tank", GuiBuilder::smallTankGui, tile, player, world, pos)
            is TileTeslaTower           -> autoContainer("tesla_tower", GuiBuilder::teslaTowerGui, tile, player, world, pos)
            is TileSolarPanel           -> autoContainer("solar_panel", GuiBuilder::solarPanelGui, tile, player, world, pos)
            is TileTransposer           -> autoContainer("transposer", GuiBuilder::transposerGui, tile, player, world, pos)
            is TileComputer             -> autoContainer("computer", GuiBuilder::computerGui, tile, player, world, pos)
            is TileMiningRobot          -> autoContainer("mining_robot", GuiBuilder::miningRobotGui, tile, player, world, pos)
            is TileBigElectricFurnace   -> autoContainer("big_electric_furnace", GuiBuilder::bigElectricFurnaceGui, tile, player, world, pos)
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