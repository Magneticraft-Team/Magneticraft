package com.cout970.magneticraft.gui

import com.cout970.magneticraft.gui.client.*
import com.cout970.magneticraft.gui.client.core.GuiBase
import com.cout970.magneticraft.gui.common.*
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.tileentity.*
import com.cout970.magneticraft.tileentity.modules.ModuleShelvingUnitMb
import com.cout970.magneticraft.tileentity.multiblock.*
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
            is ContainerElectricHeater    -> guiOf(container, ::guiElectricHeater)
            is ContainerBattery           -> guiOf(container, ::guiBattery)
            is ContainerElectricFurnace   -> guiOf(container, ::guiElectricFurnace)
            is ContainerThermopile        -> guiOf(container, ::guiThermopile)
            is ContainerWindTurbine       -> guiOf(container, ::guiWindTurbine)
            is ContainerCombustionChamber -> guiOf(container, ::guiCombustionChamber)
            is ContainerBox               -> guiOf(container, ::guiBox)
            is ContainerGrinder           -> guiOf(container, ::guiGrinder)
            is ContainerSieve             -> guiOf(container, ::guiSieve)
            is ContainerSolarTower        -> guiOf(container, ::guiSolarTower)
            is ContainerContainer         -> guiOf(container, ::guiContainer)
            is ContainerPumpjack          -> guiOf(container, ::guiPumpjack)
            is ContainerShelvingUnit      -> guiOf(container, ::guiShelvingUnit)
            is ContainerComputer          -> guiOf(container, ::guiComputer)
            is ContainerMiningRobot       -> guiOf(container, ::guiMiningRobot)
            is ContainerHydraulicPress    -> guiOf(container, ::guiHydraulicPress)
            is ContainerSteamBoiler       -> guiOf(container, ::guiSteamBoiler)
            is ContainerOilHeater         -> guiOf(container, ::guiOilHeater)
            is ContainerRefinery          -> guiOf(container, ::guiRefinery)
            is ContainerRfHeater          -> guiOf(container, ::guiRfHeater)
            else -> null
        }
        // @formatter:on
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)

        if (ID == -2) {
            return ContainerGuideBook(player, world, pos)
        }

        val tile = world.getTileEntity(pos)

        // @formatter:off
        return when (tile) {
            is TileBox               -> ContainerBox(tile, player, world, pos)
            is TileShelvingUnit      -> ContainerShelvingUnit(tile, player, world, pos, ModuleShelvingUnitMb.Level.values()[ID])
            is TileBattery           -> ContainerBattery(tile, player, world, pos)
            is TileElectricFurnace   -> ContainerElectricFurnace(tile, player, world, pos)
            is TileComputer          -> ContainerComputer(tile, player, world, pos)
            is TileMiningRobot       -> ContainerMiningRobot(tile, player, world, pos)
            is TileCombustionChamber -> ContainerCombustionChamber(tile, player, world, pos)
            is TileThermopile        -> ContainerThermopile(tile, player, world, pos)
            is TileGrinder           -> ContainerGrinder(tile, player, world, pos)
            is TileSieve             -> ContainerSieve(tile, player, world, pos)
            is TileWindTurbine       -> ContainerWindTurbine(tile, player, world, pos)
            is TileSolarTower        -> ContainerSolarTower(tile, player, world, pos)
            is TileContainer         -> ContainerContainer(tile, player, world, pos)
            is TilePumpjack          -> ContainerPumpjack(tile, player, world, pos)
            is TileElectricHeater    -> ContainerElectricHeater(tile, player, world, pos)
            is TileHydraulicPress    -> ContainerHydraulicPress(tile, player, world, pos)
            is TileSteamBoiler       -> ContainerSteamBoiler(tile, player, world, pos)
            is TileOilHeater         -> ContainerOilHeater(tile, player, world, pos)
            is TileRefinery          -> ContainerRefinery(tile, player, world, pos)
            is TileRfHeater          -> ContainerRfHeater(tile, player, world, pos)
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