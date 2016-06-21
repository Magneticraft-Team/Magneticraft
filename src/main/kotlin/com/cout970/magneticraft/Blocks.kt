package com.cout970.magneticraft

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import net.minecraftforge.fml.common.registry.GameRegistry

val blocks = listOf(
        BlockCrushingTable,
        BlockOre,
        BlockLimestone,
        BlockBurnLimestone,
        BlockTableSieve
)

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it)
        GameRegistry.register(ItemBlockBase(it))
    }
}
