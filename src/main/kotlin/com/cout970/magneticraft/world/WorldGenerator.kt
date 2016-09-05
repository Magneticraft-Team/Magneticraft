package com.cout970.magneticraft.world

import com.cout970.magneticraft.block.decoration.BlockLimestone
import com.cout970.magneticraft.block.BlockOre
import com.cout970.magneticraft.config.Config
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkGenerator
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * Created by cout970 on 15/05/2016.
 */
object WorldGenerator : IWorldGenerator {

    val generators = mutableListOf<IWorldGenerator>()

    fun init() {
        generators.clear()
        generators.add(OreGenerator(BlockOre.defaultState.withProperty(BlockOre.ORE_STATES, BlockOre.OreStates.COPPER), Config.copperOre))
        generators.add(OreGenerator(BlockOre.defaultState.withProperty(BlockOre.ORE_STATES, BlockOre.OreStates.LEAD), Config.leadOre))
        generators.add(OreGenerator(BlockOre.defaultState.withProperty(BlockOre.ORE_STATES, BlockOre.OreStates.COBALT), Config.cobaltOre))
        generators.add(OreGenerator(BlockOre.defaultState.withProperty(BlockOre.ORE_STATES, BlockOre.OreStates.TUNGSTEN), Config.tungstenOre))
        generators.add(GaussianOreGenerator(BlockLimestone.defaultState.withProperty(BlockLimestone.LIMESTONE_STATES, BlockLimestone.LimestoneStates.NORMAL), Config.limestone))
    }

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        if (world == null || random == null) return
        generators.forEach {
            it.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)
        }
    }
}