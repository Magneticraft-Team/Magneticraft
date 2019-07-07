package com.cout970.magneticraft.registry

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.Magneticraft
import com.cout970.magneticraft.misc.RegisterBlocks
import com.cout970.magneticraft.misc.info
import com.cout970.magneticraft.misc.logError
import com.cout970.magneticraft.systems.blocks.IBlockMaker
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

    val data = Magneticraft.asmData.getAll(RegisterBlocks::class.java.canonicalName)

    data.forEach {
        try {
            @Suppress("UNCHECKED_CAST")
            val clazz = Class.forName(it.className) as Class<IBlockMaker>
            val instance = clazz.kotlin.objectInstance

            if (instance != null) blockList += instance.initBlocks()

            if (Debug.DEBUG) {
                if (instance == null) logError("Unable to find instance for: ${clazz.canonicalName}")
                info("Registering Blocks: ${clazz.`package`.name}")
            }
        } catch (e: Exception) {
            logError("Error auto-registering tileEntity: $it")
            e.printStackTrace()
        }
    }

    blockList.forEach { registry.register(it.first) }
    blocks = blockList
}

//listOf<Block>(
//        BlockKiln,
//        BlockKilnShelf,
//        BlockIcebox,
//        BlockCoke,
//        BlockGlazedBrick,
//        BlockFluxedGravel,
//        BlockCharcoalSlab,
//        BlockInfiniteHeat,
//        BlockBrickFurnace,
//        BlockFirebox,
//        BlockHeatReservoir,
