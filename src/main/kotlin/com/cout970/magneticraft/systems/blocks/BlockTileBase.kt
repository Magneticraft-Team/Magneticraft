package com.cout970.magneticraft.systems.blocks

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

open class BlockTileBase(
    val factory: (World, IBlockState) -> TileEntity?,
    val filter: ((IBlockState) -> Boolean)?,
    material: Material
) : BlockBase(material), ITileEntityProvider {

    @Suppress("DEPRECATION")
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        val state = getStateFromMeta(meta)
        filter?.let {
            if (!it.invoke(state)) {
                return null
            }
        }
        return factory(worldIn, state)
    }

    override fun hasTileEntity(state: IBlockState): Boolean {
        return filter?.invoke(state) ?: super.hasTileEntity(state)
    }
}