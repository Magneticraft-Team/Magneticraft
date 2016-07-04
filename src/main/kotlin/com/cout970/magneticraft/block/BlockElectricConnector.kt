package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.worlds.getTile
import com.cout970.magneticraft.tileentity.electric.TileElectricConnector
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentString
import net.minecraft.world.World

/**
 * Created by cout970 on 29/06/2016.
 */
object BlockElectricConnector :BlockBase(Material.IRON, "electric_connector"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileElectricConnector()

    override fun onBlockActivated(worldIn: World?, pos: BlockPos?, state: IBlockState?, playerIn: EntityPlayer?, hand: EnumHand?, heldItem: ItemStack?, side: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        val tile = worldIn!!.getTile<TileElectricConnector>(pos!!)
        if(tile != null){
            tile.state = (tile.state + 1) % 3
            if(!worldIn.isRemote)
            playerIn?.addChatComponentMessage(TextComponentString(tile.state.toString()))
        }
        return true
    }
}