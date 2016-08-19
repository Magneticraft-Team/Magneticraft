package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.itemblock.*
import net.minecraftforge.fml.common.registry.GameRegistry

//@formatter:off
val blocks = mapOf(
        BlockCrushingTable          to ItemBlockBase(BlockCrushingTable),
        BlockTableSieve             to ItemBlockBase(BlockTableSieve),
        BlockOre                    to ItemBlockBase(BlockOre),
        BlockLimestone              to ItemBlockBase(BlockLimestone),
        BlockBurntLimestone         to ItemBlockBase(BlockBurntLimestone),
        BlockFeedingTrough          to ItemBlockFeedingTrough(),
        BlockElectricConnector      to ItemBlockBase(BlockElectricConnector),
        BlockElectricPole           to ItemBlockElectricPole(),
        BlockIncendiaryGenerator    to ItemBlockIncendiaryGenerator(),
        BlockElectricFurnace        to ItemBlockBase(BlockElectricFurnace),
        BlockElectricPoleAdapter    to ItemBlockElectricPoleAdapter(),
        BlockBattery                to ItemBlockBase(BlockBattery),
        BlockInfiniteWater          to ItemBlockBase(BlockInfiniteWater),
        BlockTileLimestone          to ItemBlockBase(BlockTileLimestone),
        BlockInfiniteEnergy         to ItemBlockBase(BlockInfiniteEnergy),
        BlockAirLock                to ItemBlockBase(BlockAirLock),
        BlockAirBubble              to ItemBlockBase(BlockAirBubble)
)
//@formatter:on

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it.key)
        GameRegistry.register(it.value)
    }
}
