package com.cout970.magneticraft.block

import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState


/**
 * Created by cout970 on 30/06/2016.
 */
abstract class BlockState(material: Material, name: String) : BlockBase(material, name) {

    abstract override fun getMetaFromState(state: IBlockState): Int

    abstract override fun getStateFromMeta(meta: Int): IBlockState?

    abstract override fun createBlockState(): BlockStateContainer
}