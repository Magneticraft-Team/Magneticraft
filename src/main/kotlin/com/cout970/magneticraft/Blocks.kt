package com.cout970.magneticraft

import com.cout970.magneticraft.block.BlockCrushingTable
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import net.minecraftforge.fml.common.registry.GameRegistry

val blocks = listOf(
    BlockCrushingTable
)

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it)
        GameRegistry.register(ItemBlockBase(it))
    }
}
