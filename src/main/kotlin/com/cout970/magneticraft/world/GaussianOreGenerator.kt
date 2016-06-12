package com.cout970.magneticraft.world

import com.cout970.magneticraft.config.GaussOreConfig
import com.cout970.magneticraft.util.vector.Vec2d
import net.minecraft.block.state.IBlockState
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkGenerator
import net.minecraft.world.chunk.IChunkProvider
import java.util.*

/**
 * Created by cout970 on 11/06/2016.
 */
class GaussianOreGenerator(
        blockstate: IBlockState,
        val conf: GaussOreConfig
) : OreGenerator(
        blockstate,
        conf) {

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        if (world == null || random == null) return
        if (world.provider.dimension in listOf(1, -1)) return

        if (conf.active) {
            val nextGaussian = random.nextGaussian() * conf.deviation + conf.chunkAmount
            var veins = Math.floor(nextGaussian).toInt()
            if (veins < 0) {
                veins = 0
            }
            if (veins < conf.minAmountPerChunk) {
                veins = conf.minAmountPerChunk
            }
            if (veins > conf.maxAmountPerChunk) {
                veins = conf.maxAmountPerChunk
            }
            generateChunkOres(world, Vec2d(chunkX, chunkZ), random, veins)
        }
    }
}