package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/03/26.
 */


var blocks: List<Pair<Block, ItemBlock>> = emptyList()
    private set

fun initBlocks() {
    val blocks_ = mutableListOf<Pair<Block, ItemBlock>>()

    blocks_ += Decoration.initBlocks()
    blocks_ += Ores.initBlocks()
    blocks_ += Machines.initBlocks()
    blocks_ += MultiblockParts.initBlocks()
    blocks_ += ElectricMachines.initBlocks()
    blocks_ += Multiblocks.initBlocks()

    val blockRegistry = GameRegistry.findRegistry(Block::class.java)
    val itemRegistry = GameRegistry.findRegistry(Item::class.java)
    blocks_.forEach { blockRegistry.register(it.first); itemRegistry .register(it.second) }
    blocks = blocks_
}


//listOf<Block>(
//        BlockCrushingTable,
//        BlockTableSieve,
//        BlockOre,
//        BlockLimestone,
//        BlockBurntLimestone,
//        BlockWoodChip,
//        BlockFiberboard,
//        BlockFeedingTrough,
//        BlockElectricConnector,
//        BlockElectricPole,
//        BlockIncendiaryGenerator,
//        BlockElectricFurnace,
//        BlockElectricPoleAdapter,
//        BlockBattery,
//        BlockInfiniteWater,
//        BlockTileLimestone,
//        BlockInfiniteEnergy,
//        BlockInfiniteCold,
//        BlockAirLock,
//        BlockAirBubble,
//        BlockHydraulicPress,
//        BlockKiln,
//        BlockKilnShelf,
//        BlockGrinder,
//        BlockSifter,
//        BlockStripedMachineBlock,
//        BlockMachineBlockSupportColumn,
//        BlockMachineBlock,
//        BlockMesh,
//        BlockCompactedCopper,
//        BlockCompactedCobalt,
//        BlockCompactedLead,
//        BlockCompactedTungsten,
//        BlockSolarPanel,
//        BlockElectricalMachineBlock,
//        BlockIcebox,
//        BlockCoke,
//        BlockGlazedBrick,
//        BlockFluxedGravel,
//        BlockCharcoalSlab,
//        BlockInfiniteHeat,
//        BlockBrickFurnace,
//        BlockFirebox,
//        BlockHeatSink,
//        BlockHeatReservoir,
//        BlockHeatPipe,
//        BlockRedstoneHeatPipe,
//        BlockElectricHeater,
//        BlockComputer
//)
