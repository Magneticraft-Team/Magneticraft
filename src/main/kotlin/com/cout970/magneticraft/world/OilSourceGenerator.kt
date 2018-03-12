package com.cout970.magneticraft.world

import com.cout970.magneticraft.config.OilGenConfig
import com.cout970.magneticraft.util.iterateArea
import com.cout970.magneticraft.util.vector.vec2Of
import com.cout970.magneticraft.util.vector.vec3Of
import com.cout970.vector.extensions.distanceSq
import com.google.common.base.Predicate
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkProvider
import net.minecraft.world.gen.IChunkGenerator
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * Created by cout970 on 2017/09/29.
 */
class OilSourceGenerator(
        val blockstate: IBlockState,
        val conf: OilGenConfig
) : IWorldGenerator {

    companion object {

        val replaceable = Predicate<IBlockState> { input -> input?.block == Blocks.STONE }
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
                          chunkGenerator: IChunkGenerator,
                          chunkProvider: IChunkProvider) {

        if (!conf.active) return

        val sectorX = chunkX shr 3
        val sectorZ = chunkZ shr 3

        if (sectorX % conf.distance == 0 && sectorZ % conf.distance == 0) {
            placeBlocks(random, chunkX, chunkZ, world)
        }
    }

    fun placeBlocks(random: Random, chunkX: Int, chunkZ: Int, world: World) {

        val sectorX = chunkX shr 3
        val sectorZ = chunkZ shr 3

        val sign = vec2Of(Math.signum(chunkX.toFloat()), Math.signum(chunkZ.toFloat()))
        val centerX = (sectorX * 8 - 4 * sign.xi) * 16 + 8
        val centerZ = (sectorZ * 8 - 4 * sign.yi) * 16 + 8
        val center = vec3Of(centerX, 0, centerZ)

        // do nothing if the chunk is too far away
        if (vec3Of(chunkX * 16 + 16, 0, chunkZ * 16 + 16).distanceSq(center) > 1188) {
            return
        }

        println("Generating at: $chunkX, $chunkZ")

        val height = 8 + (sectorX and 3) + (sectorZ and 3)

        for (k in -4..4) {

            iterateArea(0..15, 0..15) { i, j ->
                val x = chunkX * 16 + i + 8
                val z = chunkZ * 16 + j + 8
                val y = height + k

                val radiusSq = 1024 - Math.abs(k) * 32

                if (vec3Of(x, 0, z).distanceSq(center) < radiusSq) {

                    if (random.nextFloat() < conf.prob) {

                        val pos = BlockPos(x, y, z)
                        val currentBlock = world.getBlockState(pos)

                        if (currentBlock.block.isReplaceableOreGen(currentBlock, world, pos, replaceable)) {
                            world.setBlockState(pos, blockstate, 2)
                        }
                    }
                }
            }
        }
    }
}