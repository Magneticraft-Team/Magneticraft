package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.heat.*
import com.cout970.magneticraft.block.itemblock.*
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.multiblock.BlockSolarPanel
import net.minecraftforge.fml.common.registry.GameRegistry

//Map with all the blocks, the keys are Blocks and the values ItemBlocks
val blocks = mapOf(
        withItemBlock(BlockCrushingTable),
        withItemBlock(BlockTableSieve),
        withItemBlock(BlockOre),
        withItemBlock(BlockLimestone),
        withItemBlock(BlockBurntLimestone),
        BlockFeedingTrough to ItemBlockFeedingTrough(),
        withItemBlock(BlockElectricConnector),
        BlockElectricPole to ItemBlockElectricPole(),
        BlockIncendiaryGenerator to ItemBlockIncendiaryGenerator(),
        withItemBlock(BlockElectricFurnace),
        BlockElectricPoleAdapter to ItemBlockElectricPoleAdapter(),
        withItemBlock(BlockBattery),
        withItemBlock(BlockBrickFurnace),
        withItemBlock(BlockFirebox),
        withItemBlock(BlockHeatSink),
        withItemBlock(BlockHeatReservoir),
        withItemBlock(BlockInfiniteWater),
        withItemBlock(BlockTileLimestone),
        withItemBlock(BlockInfiniteEnergy),
        withItemBlock(BlockInfiniteHeat),
        withItemBlock(BlockInfiniteCold),
        withItemBlock(BlockElectricHeater),
        withItemBlock(BlockAirLock),
        withItemBlock(BlockAirBubble),
        withItemBlock(BlockHydraulicPress),
        withItemBlock(BlockStripedMachineBlock),
        withItemBlock(BlockMachineBlockSupportColumn),
        withItemBlock(BlockMachineBlock),
        withItemBlock(BlockCompactedCopper),
        withItemBlock(BlockCompactedCobalt),
        withItemBlock(BlockCompactedLead),
        withItemBlock(BlockCompactedTungsten),
        withItemBlock(BlockSolarPanel),
        withItemBlock(BlockElectricalMachineBlock),
        withItemBlock(BlockCoke)
)

private fun withItemBlock(blockBase: BlockBase) = blockBase to ItemBlockBase(blockBase)

/**
 * Registers all the blocks in the mod, called by CommonProxy
 */
fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it.key)
        GameRegistry.register(it.value)
    }
}
