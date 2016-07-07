package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.ElectricPoleTypes
import com.cout970.magneticraft.tileentity.electric.TileElectricPole
import com.cout970.magneticraft.util.get
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 30/06/2016.
 */

val ELECTRIC_POLE_PLACE: PropertyEnum<ElectricPoleTypes> = PropertyEnum.create("place", ElectricPoleTypes::class.java)

object BlockElectricPole : BlockElectricPoleBase(Material.WOOD, "electric_pole"), ITileEntityProvider {

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity? {
        if(ELECTRIC_POLE_PLACE[getStateFromMeta(meta)!!].isMainBlock()){
            return TileElectricPole()
        }
        return null
    }
}