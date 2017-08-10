package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraftforge.fml.common.registry.ForgeRegistries

/**
 * Created by cout970 on 2017/03/26.
 */


var blocks: List<Pair<Block, ItemBlock>> = emptyList()
    private set

fun initBlocks() {
    val blocks_ = mutableListOf<Pair<Block, ItemBlock>>()

    blocks_ += Decoration.initBlocks()
    blocks_ += Ores.initBlocks()
    blocks_ += ManualMachines.initBlocks()
    blocks_ += MultiblockParts.initBlocks()
    blocks_ += ElectricMachines.initBlocks()
    blocks_ += Multiblocks.initBlocks()
    blocks_ += Computers.initBlocks()
    blocks_ += HeatMachines.initBlocks()
    blocks_ += AutomaticMachines.initBlocks()

    blocks_.forEach { ForgeRegistries.BLOCKS.register(it.first); ForgeRegistries.ITEMS.register(it.second) }
    blocks = blocks_
}

//listOf<Block>(
//        BlockWoodChip,
//        BlockFiberboard,
//        BlockFeedingTrough,
//        BlockIncendiaryGenerator,
//        BlockInfiniteCold,
//        BlockAirLock,
//        BlockAirBubble,
//        BlockHydraulicPress,
//        BlockKiln,
//        BlockKilnShelf,
//        BlockGrinder,
//        BlockSifter,
//        BlockCompactedCopper,
//        BlockCompactedCobalt,
//        BlockCompactedLead,
//        BlockCompactedTungsten,
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
//        BlockElectricHeater
//)
