@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.itemblock.ItemBlockElectricPole
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemBlock
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object BlockElectricPole : BlockElectricPoleBase(Material.WOOD, "electric_pole"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if(getStateFromMeta(meta)!![ELECTRIC_POLE_PLACE].isMainBlock()){
            return TileElectricPole()
        }
        return null
    }

    override fun createItemForm(): ItemBlock? {
        return ItemBlockElectricPole(this)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return state[ELECTRIC_POLE_PLACE].isMainBlock()
    }
}