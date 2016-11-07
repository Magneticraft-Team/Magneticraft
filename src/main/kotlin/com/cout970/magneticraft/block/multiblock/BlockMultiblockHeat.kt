package com.cout970.magneticraft.block.multiblock

import com.cout970.magneticraft.block.heat.IHeatBlock
import net.minecraft.block.material.Material
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 21/08/2016.
 */
abstract class BlockMultiblockHeat(material: Material, name: String) : BlockMultiblock(material, name), IHeatBlock {

    override fun onNeighborChange(world: IBlockAccess?, pos: BlockPos?, neighbor: BlockPos?) {
        heatNeighborCheck(world, pos, neighbor)
        super.onNeighborChange(world, pos, neighbor)
    }

    override fun tickRate(worldIn: World?): Int {
        return 1
    }
}