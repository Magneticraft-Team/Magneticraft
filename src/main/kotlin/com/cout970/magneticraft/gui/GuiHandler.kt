package com.cout970.magneticraft.gui

import com.cout970.magneticraft.gui.client.blocks.*
import com.cout970.magneticraft.gui.common.ContainerBase
import com.cout970.magneticraft.gui.common.blocks.*
import com.cout970.magneticraft.tileentity.computer.TileComputer
import com.cout970.magneticraft.tileentity.electric.TileBattery
import com.cout970.magneticraft.tileentity.electric.TileElectricFurnace
import com.cout970.magneticraft.tileentity.electric.TileIncendiaryGenerator
import com.cout970.magneticraft.tileentity.heat.TileBrickFurnace
import com.cout970.magneticraft.tileentity.heat.TileFirebox
import com.cout970.magneticraft.tileentity.heat.TileIcebox
import com.cout970.magneticraft.tileentity.multiblock.TileGrinder
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

/**
 * This class handles which GUI should be opened when a block or item calls player.openGui(...)
 */
object GuiHandler : IGuiHandler {

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        val serverElement = getServerGuiElement(ID, player, world, x, y, z) as ContainerBase
        return when (tile) {
            is TileIncendiaryGenerator -> GuiIncendiaryGenerator(serverElement)
            is TileBattery -> GuiBattery(serverElement)
            is TileElectricFurnace -> GuiElectricFurnace(serverElement)
            is TileComputer -> GuiComputer(tile, serverElement as ContainerMonitor)
            is TileFirebox -> GuiFirebox(serverElement)
            is TileIcebox -> GuiIcebox(serverElement)
            is TileBrickFurnace -> GuiBrickFurnace(serverElement)
            is TileGrinder -> GuiGrinder(serverElement)
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        return when (tile) {
            is TileIncendiaryGenerator -> ContainerIncendiaryGenerator(player, world, BlockPos(x, y, z))
            is TileBattery -> ContainerBattery(player, world, BlockPos(x, y, z))
            is TileElectricFurnace -> ContainerElectricFurnace(player, world, BlockPos(x, y, z))
            is TileGrinder -> ContainerGrinder(player, world, BlockPos(x, y, z))
            is TileBrickFurnace -> ContainerBrickFurnace(player, world, BlockPos(x, y, z))
            is TileFirebox -> ContainerFirebox(player, world, BlockPos(x, y, z))
            is TileIcebox -> ContainerIcebox(player, world, BlockPos(x, y, z))
            is TileComputer -> ContainerMonitor(tile, player, world, BlockPos(x, y, z))
            else -> null
        }
    }
}