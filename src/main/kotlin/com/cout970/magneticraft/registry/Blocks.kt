package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.itemblock.*
import com.cout970.magneticraft.block.multiblock.BlockGrinder
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.multiblock.BlockSolarPanel
import net.minecraftforge.fml.common.registry.GameRegistry


val blocks = mapOf(
        pair(BlockCrushingTable),
        pair(BlockTableSieve),
        pair(BlockOre),
        pair(BlockLimestone),
        pair(BlockBurntLimestone),
        BlockFeedingTrough to ItemBlockFeedingTrough(),
        pair(BlockElectricConnector),
        BlockElectricPole to ItemBlockElectricPole(),
        BlockIncendiaryGenerator to ItemBlockIncendiaryGenerator(),
        pair(BlockElectricFurnace),
        BlockElectricPoleAdapter to ItemBlockElectricPoleAdapter(),
        pair(BlockBattery),
        pair(BlockInfiniteWater),
        pair(BlockTileLimestone),
        pair(BlockInfiniteEnergy),
        pair(BlockAirLock),
        pair(BlockAirBubble),
        pair(BlockHydraulicPress),
        pair(BlockGrinder),
        pair(BlockStripedMachineBlock),
        pair(BlockMachineBlockSupportColumn),
        pair(BlockMachineBlock),
        pair(BlockCompactedCopper),
        pair(BlockCompactedCobalt),
        pair(BlockCompactedLead),
        pair(BlockCompactedTungsten),
        pair(BlockSolarPanel),
        pair(BlockElectricalMachineBlock)
//        BlockInserter               to ItemBlockBase(BlockInserter)
)

private fun pair(blockBase: BlockBase) = blockBase to ItemBlockBase(blockBase)

fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it.key)
        GameRegistry.register(it.value)
    }
}
