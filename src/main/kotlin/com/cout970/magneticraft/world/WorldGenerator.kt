package com.cout970.magneticraft.world

import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.config.Config
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * Created by cout970 on 15/05/2016.
 */
object WorldGenerator : IWorldGenerator {

    val generators = mutableListOf<IWorldGenerator>()

    fun init() {
        generators.clear()
        Ores.OreType.COPPER.getBlockState(Ores.ores)
        generators.add(OreGenerator(Ores.OreType.COPPER.getBlockState(Ores.ores), Config.copperOre))
        generators.add(OreGenerator(Ores.OreType.LEAD.getBlockState(Ores.ores), Config.leadOre))
        generators.add(OreGenerator(Ores.OreType.COBALT.getBlockState(Ores.ores), Config.cobaltOre))
        generators.add(OreGenerator(Ores.OreType.TUNGSTEN.getBlockState(Ores.ores), Config.tungstenOre))
        generators.add(GaussianOreGenerator(Decoration.LimestoneKind.NORMAL.getBlockState(Decoration.limestone), Config.limestone))
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator, chunkProvider: IChunkProvider) {
        generators.forEach {
            it.generate(random, chunkX, chunkZ, world, chunkGenerator, chunkProvider)
        }
    }
}