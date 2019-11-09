package com.cout970.magneticraft.systems.blocks

import com.cout970.magneticraft.IBlockState
import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.IBlockReader

open class BlockTileBase(
    val factory: (IBlockReader, IBlockState) -> TileEntity?,
    val filter: ((IBlockState) -> Boolean)?,
    props: Block.Properties
) : BlockBase(props) {

    override fun createTileEntity(state: BlockState, world: IBlockReader): TileEntity? {
        filter?.let { valid ->
            if (!valid(state)) return null
        }
        return factory(world, state)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return filter?.invoke(state) ?: true
    }
}