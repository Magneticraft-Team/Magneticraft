package com.cout970.magneticraft.world

import com.cout970.magneticraft.util.WorldRef
import com.google.common.base.Predicate
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by cout970 on 15/05/2016.
 */

class OreGenerator(
        val ore: IBlockState,
        val blocksPerVein: Int,
        val predicate: Predicate<IBlockState>) {

    constructor(ore: Block, number: Int) : this(ore.defaultState, number, Predicate { input -> input?.block == Blocks.STONE })

    constructor(ore: IBlockState, number: Int) : this(ore, number, Predicate { input -> input?.block == Blocks.STONE })

    constructor(ore: Block, meta: Int, number: Int, target: Predicate<IBlockState>) : this(ore.getStateFromMeta(meta), number, target)

    fun generate(ref: WorldRef, random: Random) {
        generate(ref.world, random, ref.pos.x, ref.pos.y, ref.pos.z)
    }

    fun generate(world: World, random: Random, x: Int, y: Int, z: Int) {
        val angle = random.nextFloat() * Math.PI.toFloat()
        val posX = x + 8 + Math.sin(angle.toDouble()) * blocksPerVein / 8.0f
        val negX = x + 8 - Math.sin(angle.toDouble()) * blocksPerVein / 8.0f
        val posZ = z + 8 + Math.cos(angle.toDouble()) * blocksPerVein / 8.0f
        val negZ = z + 8 - Math.cos(angle.toDouble()) * blocksPerVein / 8.0f
        val y1 = (y + random.nextInt(3) - 2).toDouble()
        val y2 = (y + random.nextInt(3) - 2).toDouble()

        for (n in 0..blocksPerVein) {

            val xPlace = posX + (negX - posX) * n / blocksPerVein
            val yPlace = y1 + (y2 - y1) * n / blocksPerVein
            val zPlace = posZ + (negZ - posZ) * n / blocksPerVein
            val scale = random.nextDouble() * blocksPerVein / 16.0
            val desp = (Math.sin(n * Math.PI / blocksPerVein) + 1.0f) * scale + 1.0

            val minX = floor_double(xPlace - desp / 2.0)
            val minY = floor_double(yPlace - desp / 2.0)
            val minZ = floor_double(zPlace - desp / 2.0)
            val maxX = floor_double(xPlace + desp / 2.0)
            val maxY = floor_double(yPlace + desp / 2.0)
            val maxZ = floor_double(zPlace + desp / 2.0)

            for (i in minX..maxX) {
                val xDistance = (i.toDouble() + 0.5 - xPlace) / (desp / 2.0)

                if (xDistance * xDistance < 1.0) {
                    for (j in minY..maxY) {
                        val yDistance = (j.toDouble() + 0.5 - yPlace) / (desp / 2.0)

                        if (xDistance * xDistance + yDistance * yDistance < 1.0) {
                            for (k in minZ..maxZ) {

                                val zDistance = (k.toDouble() + 0.5 - zPlace) / (desp / 2.0)
                                val blockPos = BlockPos(i, j, k)

                                if (xDistance * xDistance + yDistance * yDistance + zDistance * zDistance < 1.0) {
                                    val ref = WorldRef(world, blockPos)
                                    val state = ref.getBlockState()
                                    if (state.block.isReplaceableOreGen(state, world, blockPos, predicate)) {
                                        ref.setBlockState(ore, 2)
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