package com.cout970.magneticraft.systems.worldgen

import com.cout970.magneticraft.misc.iterateArea
import com.cout970.magneticraft.misc.vector.*
import com.cout970.magneticraft.systems.config.OilGenConfig
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
    val config: OilGenConfig
) : IWorldGenerator {

    companion object {

        val replaceable = Predicate<IBlockState> { input -> input?.block == Blocks.STONE }
    }

    override fun generate(random: Random, chunkX: Int, chunkZ: Int, world: World,
                          chunkGenerator: IChunkGenerator,
                          chunkProvider: IChunkProvider) {

        if (!config.active) return

        val sectorX = chunkX shr 4
        val sectorZ = chunkZ shr 4

        if (sectorX % config.distance == 0 && sectorZ % config.distance == 0) {
            placeBlocks(random, chunkX, chunkZ, world)
        }
    }

    fun placeBlocks(random: Random, chunkX: Int, chunkZ: Int, world: World) {

        // sectors are 16x16 chunks
        val sectorX = chunkX shr 4
        val sectorZ = chunkZ shr 4

        val start = vec3Of((sectorX shl 4) * 16, 0, (sectorZ shl 4) * 16)
        val size = vec3Of(16 * 16, 0, 16 * 16)

        val center = start + size * 0.5
        val centerBlock = (center).toBlockPos()

        // chunk pos in block coordinates
        val chunkPos = BlockPos((chunkX shl 4), 0, (chunkZ shl 4))
        val outerRadiusSq = (5.5 * 16) * (5.5 * 16) // 5.5 chunk radius

        // do nothing if the chunk is too far away
        if (chunkPos.toVec3d().squareDistanceTo(center) > outerRadiusSq) {
            return
        }

        val innerRadiusSq = (5 * 16) * (5 * 16) // 5 chunk radius
        val height = 20

        iterateArea(0..15, 0..15) { i, j ->

            if (random.nextInt(3) == 0) {

                // the +8 is needed to avoid cascading chunk updates see Chunk#populate
                val pos = chunkPos.add(i + 8, 0, j + 8)
                val dist = centerBlock.distanceSq(pos)

                if (dist < innerRadiusSq) {
                    val elev = ((1 - dist / innerRadiusSq) * 4).toInt()
                    for (k in -elev..elev) {

                        val currentPos = pos.up(height + k)
                        val currentBlock = world.getBlockState(currentPos)

                        if (currentBlock.block.isReplaceableOreGen(currentBlock, world, currentPos, replaceable)) {
                            // flag 2 to not send block updates when generating world
                            world.setBlockState(currentPos, blockstate, 2)
                        }
                    }
                }
            }

        }
    }
}