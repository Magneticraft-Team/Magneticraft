package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.fuel.BlockCharcoalSlab
import com.cout970.magneticraft.block.fuel.BlockCoke
import com.cout970.magneticraft.block.heat.*
import com.cout970.magneticraft.block.itemblock.*
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.multiblock.BlockKiln
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
        withItemBlock(BlockInfiniteWater),
        withItemBlock(BlockTileLimestone),
        withItemBlock(BlockInfiniteEnergy),
        withItemBlock(BlockInfiniteCold),
        withItemBlock(BlockAirLock),
        withItemBlock(BlockAirBubble),
        withItemBlock(BlockHydraulicPress),
        withItemBlock(BlockKiln),
        withItemBlock(BlockKilnShelf),
        withItemBlock(BlockStripedMachineBlock),
        withItemBlock(BlockMachineBlockSupportColumn),
        withItemBlock(BlockMachineBlock),
        withItemBlock(BlockCompactedCopper),
        withItemBlock(BlockCompactedCobalt),
        withItemBlock(BlockCompactedLead),
        withItemBlock(BlockCompactedTungsten),
        withItemBlock(BlockSolarPanel),
        withItemBlock(BlockElectricalMachineBlock),
        withItemBlock(BlockIcebox),
        withItemBlock(BlockCoke),
        withItemBlock(BlockGlazedBrick),
        withItemBlock(BlockFluxedGravel),
        withItemBlock(BlockCharcoalSlab)
)

val lightBlocks = mapOf(
        withItemBlock(BlockInfiniteHeat)
)

val tickBlocks = mapOf(
        withItemBlock(BlockBrickFurnace),
        withItemBlock(BlockFirebox),
        withItemBlock(BlockHeatSink),
        withItemBlock(BlockHeatReservoir),
        withItemBlock(BlockHeatPipe),
        withItemBlock(BlockRedstoneHeatPipe),
        withItemBlock(BlockElectricHeater)
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
    tickBlocks.forEach {
        GameRegistry.register(it.key).tickRandomly = true
        GameRegistry.register(it.value)
    }
    lightBlocks.forEach {
        GameRegistry.register(it.key).setLightLevel(1.0f)
        GameRegistry.register(it.value)
    }
}
