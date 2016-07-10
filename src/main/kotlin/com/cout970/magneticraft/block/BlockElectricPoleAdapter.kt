package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.electric.TileElectricPoleAdapter
import com.cout970.magneticraft.util.get
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 05/07/2016.
 */
object BlockElectricPoleAdapter : BlockElectricPoleBase(Material.IRON, "electric_pole_adapter"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if(ELECTRIC_POLE_PLACE[BlockElectricPole.getStateFromMeta(meta)!!].isMainBlock()){
            return TileElectricPoleAdapter()
        }
        return null
    }
}