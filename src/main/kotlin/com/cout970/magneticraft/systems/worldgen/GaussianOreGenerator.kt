package com.cout970.magneticraft.systems.worldgen

import com.cout970.magneticraft.misc.vector.Vec2d
import com.cout970.magneticraft.systems.config.GaussOreConfig
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import java.util.*

/**
 * Created by cout970 on 11/06/2016.
 */
class GaussianOreGenerator(
    blockstate: IBlockState,
    val conf: GaussOreConfig
) : OreGenerator(blockstate, conf) {

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator?,
                          chunkProvider: IChunkProvider?) {

        if (world == null || random == null) return
        if (world.provider.dimension in listOf(1, -1)) return

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