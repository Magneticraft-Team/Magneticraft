package com.cout970.magneticraft.world

import com.cout970.magneticraft.config.OreConfig
import com.cout970.magneticraft.util.vector.Vec2d
import com.google.common.base.Predicate
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraft.world.chunk.IChunkGenerator
import net.minecraft.world.chunk.IChunkProvider
import net.minecraftforge.fml.common.IWorldGenerator
import java.util.*

/**
 * Created by cout970 on 15/05/2016.
 */

open class OreGenerator(
    val ore: IBlockState,
    val predicate: Predicate<IBlockState>,
    val config: OreConfig
) : IWorldGenerator {

    constructor(ore: IBlockState, config: OreConfig) : this(ore, Predicate { input -> input?.block == Blocks.STONE }, config)

    override fun generate(random: Random?, chunkX: Int, chunkZ: Int, world: World?, chunkGenerator: IChunkGenerator?, chunkProvider: IChunkProvider?) {
        if (world == null || random == null) return
        if (world.provider.dimension in listOf(1, -1)) return
        if (config.active) generateChunkOres(world, Vec2d(chunkX, chunkZ), random, config.chunkAmount)
    }

    fun generateChunkOres(world: World, pos: Vec2d, random: Random, chunkAmount: Int) {
        for (k in 0..chunkAmount - 1) {
            val x = pos.xi * 16 + random.nextInt(16)
            val y = config.minLevel + random.nextInt(config.maxLevel - config.minLevel)
            val z = pos.yi * 16 + random.nextInt(16)
            generate(world, random, x, y, z)
        }
    }

    fun generate(world: World, random: Random, x: Int, y: Int, z: Int) {
        val angle = random.nextFloat() * Math.PI.toFloat()
        val posX = x + 8 + Math.sin(angle.toDouble()) * config.veinAmount / 8.0f
        val negX = x + 8 - Math.sin(angle.toDouble()) * config.veinAmount / 8.0f
        val posZ = z + 8 + Math.cos(angle.toDouble()) * config.veinAmount / 8.0f
        val negZ = z + 8 - Math.cos(angle.toDouble()) * config.veinAmount / 8.0f
        val y1 = (y + random.nextInt(3) - 2).toDouble()
        val y2 = (y + random.nextInt(3) - 2).toDouble()

        for (n in 0..config.veinAmount - 1) {

            val xPlace = posX + (negX - posX) * n / config.veinAmount
            val yPlace = y1 + (y2 - y1) * n / config.veinAmount
            val zPlace = posZ + (negZ - posZ) * n / config.veinAmount
            val scale = random.nextDouble() * config.veinAmount / 16.0
            val desp = (Math.sin(n * Math.PI / config.veinAmount) + 1.0f) * scale + 1.0

            val minX = floor_double(xPlace - desp / 2.0)
            val minY = floor_double(yPlace - desp / 2.0)
            val minZ = floor_double(zPlace - desp / 2.0)
            val maxX = floor_double(xPlace + desp / 2.0)
            val maxY = floor_double(yPlace + desp / 2.0)
            val maxZ = floor_double(zPlace + desp / 2.0)

            for (i in minX..maxX - 1) {
                val xDistance = (i.toDouble() + 0.5 - xPlace) / (desp / 2.0)

                if (xDistance * xDistance < 1.0) {
                    for (j in minY..maxY - 1) {
                        val yDistance = (j.toDouble() + 0.5 - yPlace) / (desp / 2.0)

                        if (xDistance * xDistance + yDistance * yDistance < 1.0) {
                            for (k in minZ..maxZ - 1) {

                                val zDistance = (k.toDouble() + 0.5 - zPlace) / (desp / 2.0)
                                val blockPos = BlockPos(i, j, k)

                                if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance < 1.0) {
                                    val state = world.getBlockState(blockPos)
                                    if (state.block.isReplaceableOreGen(state, world, blockPos, predicate)) {
                                        world.setBlockState(blockPos, ore, 2)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {

        fun floor_double(value: Double): Int {
            val i = value.toInt()
            return if (value < i.toDouble()) i - 1 else i
        }
    }
}