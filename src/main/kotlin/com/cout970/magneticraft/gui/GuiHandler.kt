package com.cout970.magneticraft.gui

import com.cout970.magneticraft.gui.client.GuiTileBattery
import com.cout970.magneticraft.gui.client.GuiTileBox
import com.cout970.magneticraft.gui.client.GuiTileElectricFurnace
import com.cout970.magneticraft.gui.common.ContainerBattery
import com.cout970.magneticraft.gui.common.ContainerBox
import com.cout970.magneticraft.gui.common.ContainerElectricFurnace
import com.cout970.magneticraft.gui.common.core.ContainerBase
import com.cout970.magneticraft.tileentity.TileBattery
import com.cout970.magneticraft.tileentity.TileBox
import com.cout970.magneticraft.tileentity.TileElectricFurnace
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
            is TileBox -> GuiTileBox(serverElement)
//            is TileIncendiaryGenerator -> GuiIncendiaryGenerator(serverElement)
            is TileBattery -> GuiTileBattery(serverElement)
            is TileElectricFurnace -> GuiTileElectricFurnace(serverElement)
//            is TileComputer -> GuiComputer(tile, serverElement as ContainerMonitor)
//            is TileFirebox -> GuiFirebox(serverElement)
//            is TileIcebox -> GuiIcebox(serverElement)
//            is TileBrickFurnace -> GuiBrickFurnace(serverElement)
//            is TileGrinder -> GuiGrinder(serverElement)
            else -> null
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val tile = world.getTileEntity(pos)
        return when (tile) {
            is TileBox -> ContainerBox(player, world, pos)
//            is TileIncendiaryGenerator -> ContainerIncendiaryGenerator(player, world, BlockPos(x, y, z))
            is TileBattery -> ContainerBattery(player, world, BlockPos(x, y, z))
            is TileElectricFurnace -> ContainerElectricFurnace(player, world, BlockPos(x, y, z))
//            is TileGrinder -> ContainerGrinder(player, world, BlockPos(x, y, z))
//            is TileBrickFurnace -> ContainerBrickFurnace(player, world, BlockPos(x, y, z))
//            is TileFirebox -> ContainerFirebox(player, world, BlockPos(x, y, z))
//            is TileIcebox -> ContainerIcebox(player, world, BlockPos(x, y, z))
//            is TileComputer -> ContainerMonitor(tile, player, world, BlockPos(x, y, z))
            else -> null
        }
    }
}