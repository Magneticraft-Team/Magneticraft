package com.cout970.magneticraft.systems.gui

import com.cout970.magneticraft.EntityPlayer
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
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.container.Container
import net.minecraft.inventory.container.INamedContainerProvider
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.StringTextComponent
import net.minecraft.world.World

/**
 * This class handles which GUI should be opened when a block or item calls player.openGui(...)
 */
object GuiHandler {

    fun getProvider(player: EntityPlayer, world: World, pos: BlockPos): INamedContainerProvider? {
        return object : INamedContainerProvider {
            override fun getDisplayName(): ITextComponent = StringTextComponent("")
            override fun createMenu(window: Int, inv: PlayerInventory, player: PlayerEntity): Container? {
                return getServerGuiElement(player, world, pos, window)
            }
        }
    }

//    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
//        val container = getServerGuiElement(ID, player, world, x, y, z) as ContainerBase
//        if (ID == -2) {
//            return GuiGuideBook(container as ContainerGuideBook)
//        }
//
//        // @formatter:off
//        return when (container) {
//            is AutoContainer                 -> AutoGui(container)
//            else -> null
//        }
//        // @formatter:on
//    }

    fun getServerGuiElement(player: EntityPlayer, world: World, pos: BlockPos, window: Int): AutoContainer? {
        @Suppress("MoveVariableDeclarationIntoWhen")
        val tile = world.getTileEntity(pos)

        // @formatter:off
        return when (tile) {
            is TileBox                  -> autoContainer("box", GuiBuilder::boxGui, tile, player, world, pos, window)
            is TileElectricHeater       -> autoContainer("electric_heater", GuiBuilder::electricHeaterGui, tile, player, world, pos, window)
            is TileCombustionChamber    -> autoContainer("combustion_chamber", GuiBuilder::combustionChamberGui, tile, player, world, pos, window)
            is TileSteamBoiler          -> autoContainer("steam_boiler", GuiBuilder::steamBoilerGui, tile, player, world, pos, window)
            is TileBattery              -> autoContainer("battery_block", GuiBuilder::batteryBlockGui, tile, player, world, pos, window)
            is TileElectricFurnace      -> autoContainer("electric_furnace", GuiBuilder::electricFurnaceGui, tile, player, world, pos, window)
            is TileBrickFurnace         -> autoContainer("brick_furnace", GuiBuilder::brickFurnaceGui, tile, player, world, pos, window)
            is TileThermopile           -> autoContainer("thermopile", GuiBuilder::thermopileGui, tile, player, world, pos, window)
            is TileWindTurbine          -> autoContainer("wind_turbine", GuiBuilder::windTurbineGui, tile, player, world, pos, window)
            is TileRfHeater             -> autoContainer("rf_heater", GuiBuilder::rfHeaterGui, tile, player, world, pos, window)
            is TileGasificationUnit     -> autoContainer("gasification_unit", GuiBuilder::gasificationUnitGui, tile, player, world, pos, window)
            is TileGrinder              -> autoContainer("grinder", GuiBuilder::grinderGui, tile, player, world, pos, window)
            is TileSieve                -> autoContainer("sieve", GuiBuilder::sieveGui, tile, player, world, pos, window)
            is TileContainer            -> autoContainer("container", GuiBuilder::containerGui, tile, player, world, pos, window)
            is TilePumpjack             -> autoContainer("pumpjack", GuiBuilder::pumpjackGui, tile, player, world, pos, window)
            is TileOilHeater            -> autoContainer("oil_heater", GuiBuilder::oilHeaterGui, tile, player, world, pos, window)
            is TileRefinery             -> autoContainer("refinery", GuiBuilder::refineryGui, tile, player, world, pos, window)
            is TileSteamEngine          -> autoContainer("steam_engine", GuiBuilder::steamEngineGui, tile, player, world, pos, window)
            is TileBigCombustionChamber -> autoContainer("big_combustion_chamber", GuiBuilder::bigCombustionChamberGui, tile, player, world, pos, window)
            is TileBigSteamBoiler       -> autoContainer("big_steam_boiler", GuiBuilder::bigSteamBoilerGui, tile, player, world, pos, window)
            is TileSteamTurbine         -> autoContainer("steam_turbine", GuiBuilder::steamTurbineGui, tile, player, world, pos, window)
            is TileRelay                -> autoContainer("relay", GuiBuilder::relayGui, tile, player, world, pos, window)
            is TileFilter               -> autoContainer("filter", GuiBuilder::filterGui, tile, player, world, pos, window)
            is TileFabricator           -> autoContainer("fabricator", GuiBuilder::fabricatorGui, tile, player, world, pos, window)
            is TileSolarTower           -> autoContainer("solar_tower", GuiBuilder::solarTowerGui, tile, player, world, pos, window)
            is TileInserter             -> autoContainer("inserter", GuiBuilder::inserterGui, tile, player, world, pos, window)
            is TileHydraulicPress       -> autoContainer("hydraulic_press", GuiBuilder::hydraulicPressGui, tile, player, world, pos, window)
            is TileShelvingUnit         -> autoContainer("shelving_unit", GuiBuilder::shelvingUnitGui, tile, player, world, pos, window)
            is TileRfTransformer        -> autoContainer("rf_transformer", GuiBuilder::rfTransformerGui, tile, player, world, pos, window)
            is TileElectricEngine       -> autoContainer("electric_engine", GuiBuilder::electricEngineGui, tile, player, world, pos, window)
            is TileAirLock              -> autoContainer("airlock", GuiBuilder::airlockGui, tile, player, world, pos, window)
            is TileSmallTank            -> autoContainer("small_tank", GuiBuilder::smallTankGui, tile, player, world, pos, window)
            is TileTeslaTower           -> autoContainer("tesla_tower", GuiBuilder::teslaTowerGui, tile, player, world, pos, window)
            is TileSolarPanel           -> autoContainer("solar_panel", GuiBuilder::solarPanelGui, tile, player, world, pos, window)
            is TileTransposer           -> autoContainer("transposer", GuiBuilder::transposerGui, tile, player, world, pos, window)
            is TileComputer             -> autoContainer("computer", GuiBuilder::computerGui, tile, player, world, pos, window)
            is TileMiningRobot          -> autoContainer("mining_robot", GuiBuilder::miningRobotGui, tile, player, world, pos, window)
            is TileBigElectricFurnace   -> autoContainer("big_electric_furnace", GuiBuilder::bigElectricFurnaceGui, tile, player, world, pos, window)
            else -> null
        }
        // @formatter:on
    }

    fun create(window: Int, inventory: PlayerInventory, buff: PacketBuffer): AutoContainer {
        return getServerGuiElement(inventory.player, inventory.player.entityWorld, inventory.player.position, window)
            ?: error("Null container")
    }
}