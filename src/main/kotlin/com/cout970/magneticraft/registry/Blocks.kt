package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.itemblock.ItemBlockBase
import com.cout970.magneticraft.block.itemblock.ItemBlockElectricPole
import com.cout970.magneticraft.block.itemblock.ItemBlockFeedingTrough
import net.minecraftforge.fml.common.registry.GameRegistry

val blocks = mapOf(
        BlockCrushingTable to ItemBlockBase(BlockCrushingTable),
        BlockOre to ItemBlockBase(BlockOre),
        BlockLimestone to ItemBlockBase(BlockLimestone),
        BlockBurntLimestone to ItemBlockBase(BlockBurntLimestone),
        BlockFeedingTrough to ItemBlockFeedingTrough(),
        BlockElectricConnector to ItemBlockBase(BlockElectricConnector),
        BlockElectricPole to ItemBlockElectricPole()
)

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it.key)
        GameRegistry.register(it.value)
    }
}
