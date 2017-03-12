package com.cout970.magneticraft.block.heat

import com.cout970.magneticraft.tileentity.heat.TileInfiniteHeatHot
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockInfiniteHeat : BlockModContainer("infinite_heat", Material.ROCK) {

    init {
        setLightLevel(1.0f)
    }

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileInfiniteHeatHot()

    override fun getLightValue(state: IBlockState?, world: IBlockAccess?, pos: BlockPos?): Int {
        return lightValue
    }
}