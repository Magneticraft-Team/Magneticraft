package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.util.get
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

object BlockElectricPole : BlockElectricPoleBase(Material.WOOD, "electric_pole"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if(ELECTRIC_POLE_PLACE[getStateFromMeta(meta)!!].isMainBlock()){
            return TileElectricPole()
        }
        return null
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return ELECTRIC_POLE_PLACE[state].isMainBlock()
    }
}