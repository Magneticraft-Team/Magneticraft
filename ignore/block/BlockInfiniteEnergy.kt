package com.cout970.magneticraft.block

import com.cout970.magneticraft.tileentity.electric.TileInfiniteEnergy
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 27/07/2016.
 */
object BlockInfiniteEnergy : BlockMod("infinite_energy", Material.IRON), ITileEntityProvider{

    override fun createNewTileEntity(worldIn: World?, meta: Int): TileEntity = TileInfiniteEnergy()
}