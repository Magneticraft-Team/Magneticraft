package com.cout970.magneticraft.registry

import com.cout970.magneticraft.block.*
import net.minecraft.block.Block
import net.minecraft.item.ItemBlock
import net.minecraftforge.registries.IForgeRegistry

/**
 * Created by cout970 on 2017/03/26.
 */


var blocks: List<Pair<Block, ItemBlock?>> = emptyList()
    private set

fun initBlocks(registry: IForgeRegistry<Block>) {
    val blockList = mutableListOf<Pair<Block, ItemBlock?>>()

    blockList += Decoration.initBlocks()
    blockList += Ores.initBlocks()
    blockList += ManualMachines.initBlocks()
    blockList += MultiblockParts.initBlocks()
    blockList += ElectricMachines.initBlocks()
    blockList += Multiblocks.initBlocks()
    blockList += Computers.initBlocks()
    blockList += HeatMachines.initBlocks()
    blockList += AutomaticMachines.initBlocks()
    blockList += ElectricConductors.initBlocks()
    blockList += FluidMachines.initBlocks()

    blockList.forEach { registry.register(it.first) }
    blocks = blockList
}

//listOf<Block>(
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
