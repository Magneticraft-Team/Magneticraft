package com.cout970.magneticraft.block.multiblock

import com.cout970.magneticraft.block.BlockMultiState
import com.cout970.magneticraft.multiblock.ITileMultiblock
import com.cout970.magneticraft.multiblock.MultiblockContext
import com.cout970.magneticraft.multiblock.MultiblockManager
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 21/08/2016.
 */
abstract class BlockMultiblock(material: Material, name: String) : BlockMultiState(material, name) {

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos)
        if (tile is ITileMultiblock) {
            if(tile.multiblock != null) {
                MultiblockManager.deactivateMultiblockStructure(MultiblockContext(tile.multiblock!!, worldIn, pos.subtract(tile.centerPos!!), tile.multiblockFacing!!, null))
            }
        }
        super.breakBlock(worldIn, pos, state)
    }
}