package com.cout970.magneticraft.systems.worldgen

import com.cout970.magneticraft.IBlockState
import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.config.GaussOreConfig
import net.minecraft.world.World
import net.minecraft.world.chunk.AbstractChunkProvider
import net.minecraft.world.dimension.DimensionType
import net.minecraft.world.gen.ChunkGenerator
import java.util.*

/**
 * Created by cout970 on 11/06/2016.
 */
class GaussianOreGenerator(
    blockstate: IBlockState,
    val conf: GaussOreConfig
) : OreGenerator(blockstate, conf) {

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?,
                          chunkGenerator: ChunkGenerator<*>, chunkProvider: AbstractChunkProvider) {

        if (world == null || random == null) return
        if (world.dimension.type == DimensionType.THE_NETHER || world.dimension.type == DimensionType.THE_END) return

        if (conf.active) {
            val randGaussian = random.nextGaussian() * conf.deviation + conf.chunkAmount

            val veins = Math.floor(randGaussian)
                .toInt()
                .coerceAtLeast(0)
                .coerceIn(conf.minAmountPerChunk, conf.maxAmountPerChunk)

//            if (config.maxAmountPerChunk == 1 && veins == 1)
//                debug(randGaussian, veins, random.nextGaussian(), config.deviation)

            if (veins > 0) {
                generateChunkOres(world, Vec2d(chunkX, chunkZ), random, veins)
            }
        }
    }
}