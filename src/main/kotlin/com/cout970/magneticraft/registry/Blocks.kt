package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.fuel.BlockCharcoalSlab
import com.cout970.magneticraft.block.fuel.BlockCoke
import com.cout970.magneticraft.block.heat.*
import com.cout970.magneticraft.block.itemblock.*
import com.cout970.magneticraft.block.multiblock.*
import net.minecraftforge.fml.common.registry.GameRegistry

//Map with all the blocks, the keys are Blocks and the values ItemBlocks
val blocks = mapOf(
        withItemBlock(BlockCrushingTable),
        withItemBlock(BlockTableSieve),
        withItemBlock(BlockOre),
        withItemBlock(BlockLimestone),
        withItemBlock(BlockBurntLimestone),
        withItemBlock(BlockWoodChip),
        withItemBlock(BlockFiberboard),
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
        withItemBlock(BlockGrinder),
        withItemBlock(BlockSifter),
        withItemBlock(BlockStripedMachineBlock),
        withItemBlock(BlockMachineBlockSupportColumn),
        withItemBlock(BlockMachineBlock),
        withItemBlock(BlockMesh),
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
        withItemBlock(BlockCharcoalSlab),

        withItemBlock(BlockInfiniteHeat),

        withItemBlockAndTick(BlockBrickFurnace),
        withItemBlockAndTick(BlockFirebox),
        withItemBlockAndTick(BlockHeatSink),
        withItemBlockAndTick(BlockHeatReservoir),
        withItemBlockAndTick(BlockHeatPipe),
        withItemBlockAndTick(BlockRedstoneHeatPipe),
        withItemBlockAndTick(BlockElectricHeater)
        withItemBlock(BlockComputer),
)

private fun withItemBlock(blockBase: BlockBase) = blockBase to ItemBlockBase(blockBase)
private fun withItemBlockAndTick(blockBase: BlockBase) = blockBase.apply { tickRandomly = true } to ItemBlockBase(blockBase)

/**
 * Registers all the blocks in the mod, called by CommonProxy
 */
fun registerBlocks() {
    blocks.forEach {
        GameRegistry.register(it.key)
        GameRegistry.register(it.value)
    }
}
