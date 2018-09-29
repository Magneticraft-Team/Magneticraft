package com.cout970.magneticraft.systems.worldgen

import com.cout970.magneticraft.systems.config.Config
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*
import com.cout970.magneticraft.features.decoration.Blocks as DecorationBlocks
import com.cout970.magneticraft.features.ores.Blocks as OreBlocks

/**
 * Created by cout970 on 15/05/2016.
 */
object WorldGenerator : IWorldGenerator {

    val generators = mutableListOf<IWorldGenerator>()

    fun init() {
        generators.clear()
        generators.add(OreGenerator(OreBlocks.OreType.COPPER.getBlockState(OreBlocks.ores), Config.copperOre))
        generators.add(OreGenerator(OreBlocks.OreType.LEAD.getBlockState(OreBlocks.ores), Config.leadOre))
        generators.add(OreGenerator(OreBlocks.OreType.TUNGSTEN.getBlockState(OreBlocks.ores), Config.tungstenOre))
        generators.add(OreGenerator(OreBlocks.OreType.PYRITE.getBlockState(OreBlocks.ores), Config.pyriteOre))
        generators.add(GaussianOreGenerator(DecorationBlocks.LimestoneKind.NORMAL.getBlockState(DecorationBlocks.limestone), Config.limestone))
        generators.add(OilSourceGenerator(OreBlocks.OilAmount.FULL_100.getBlockState(OreBlocks.oilSource), Config.oil))
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
        generators.forEach {
            it.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)
        }
    }
}