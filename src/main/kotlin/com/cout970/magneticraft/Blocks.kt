package com.cout970.magneticraft

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.util.Log
import net.minecraftforge.fml.common.registry.GameRegistry

val blocks = listOf(
        BlockCrushingTable,
        BlockOre,
        BlockLimestone,
        BlockBurnLimestone,
        BlockTableSieve,
        BlockFeedingTrough
)

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it)
        GameRegistry.register(it.createItemBlock())
    }
}
