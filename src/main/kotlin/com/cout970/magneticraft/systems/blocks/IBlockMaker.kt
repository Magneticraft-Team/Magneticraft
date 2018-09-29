package com.cout970.magneticraft.systems.blocks

import net.minecraft.block.Block
import net.minecraft.item.ItemBlock

/**
 * Created by cout970 on 2017/06/11.
 */
interface IBlockMaker {

    fun initBlocks(): List<Pair<Block, ItemBlock?>>
}