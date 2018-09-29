package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.config.Config
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.block.Block
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import com.cout970.magneticraft.features.electric_machines.Blocks as ElectricBlocks

/**
 * Created by cout970 on 2017/08/11.
 */
class ModuleAirlock(
    val node: IElectricNode,
    override val name: String = "module_airlock"
) : IModule {

    override lateinit var container: IModuleContainer

    val range = 10
    val length = 9 * 9

    override fun update() {
        super.update()

        if (world.isServer && container.shouldTick(40)) {
            if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                buildBubbles()
            } else {
                startBubbleDecay(world, pos, range, length)
            }
        }
    }

    fun buildBubbles() {
        val bubble = ElectricBlocks.airBubble
        val array = Array(range * 2 + 1) {
            Array(range * 2 + 1) {
                Array<Block?>(range * 2 + 1) { null }
            }
        }

        //fin water
        for (j in -range..range) {
            for (k in -range..range) {
                for (i in -range..range) {
                    val pos = pos.add(i, j, k)
                    var block = world.getBlockState(pos).block
                    if (i * i + j * j + k * k <= length) {
                        if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                            block = bubble
                        }
                    }
                    if (block != Blocks.AIR) {
                        array[i + range][j + range][k + range] = block
                    }
                }
            }
        }
        //remove unnecessary blocks
        for (j in -range..range) {
            for (k in -range..range) {
                for (i in -range..range) {
                    if (i * i + j * j + k * k <= length) {
                        val x = i + range
                        val y = j + range
                        val z = k + range
                        if (array[x][y][z] == bubble) {
                            if (array[x - 1][y][z] != Blocks.WATER && array[x - 1][y][z] != Blocks.FLOWING_WATER &&
                                array[x + 1][y][z] != Blocks.WATER && array[x + 1][y][z] != Blocks.FLOWING_WATER &&
                                array[x][y - 1][z] != Blocks.WATER && array[x][y - 1][z] != Blocks.FLOWING_WATER &&
                                array[x][y + 1][z] != Blocks.WATER && array[x][y + 1][z] != Blocks.FLOWING_WATER &&
                                array[x][y][z - 1] != Blocks.WATER && array[x][y][z - 1] != Blocks.FLOWING_WATER &&
                                array[x][y][z + 1] != Blocks.WATER && array[x][y][z + 1] != Blocks.FLOWING_WATER) {
                                array[x][y][z] = Blocks.AIR
                            }
                        }
                    }
                }
            }
        }
        //apply changes
        for (j in -range..range) {
            for (k in -range..range) {
                for (i in -range..range) {
                    if (i * i + j * j + k * k <= length) {
                        val x = i + range
                        val y = j + range
                        val z = k + range
                        if (array[x][y][z] == bubble) {
                            if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                                world.setBlockState(pos.add(i, j, k), ElectricBlocks.DecayMode.OFF.getBlockState(bubble))
                                node.applyPower(-Config.airlockBubbleCost, false)
                            }
                        }
                    }
                }
            }
        }
        if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
            for (j_ in -range..range) {
                for (k in -range..range) {
                    for (i in -range..range) {
                        val j = -j_
                        if (i * i + j * j + k * k <= length) {
                            val x = i + range
                            val y = j + range
                            val z = k + range
                            if (array[x][y][z] == Blocks.AIR) {
                                if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                                    world.setBlockToAir(pos.add(i, j, k))
                                    node.applyPower(-Config.airlockAirCost, false)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun startBubbleDecay(world: World, pos: BlockPos, range: Int, length: Int) {
        val bubble = ElectricBlocks.airBubble
        val stateOn = ElectricBlocks.DecayMode.ON.getBlockState(bubble)

        for (j in -range..range) {
            for (k in -range..range) {
                for (i in -range..range) {
                    if (i * i + j * j + k * k <= length) {
                        val state = world.getBlockState(pos.add(i, j, k))
                        if (state.block == ElectricBlocks.airBubble) {
                            if (state[ElectricBlocks.PROPERTY_DECAY_MODE] == ElectricBlocks.DecayMode.OFF) {
                                world.setBlockState(pos.add(i, j, k), stateOn)
                                world.scheduleUpdate(pos.add(i, j, k), bubble, 0)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBreak() {
        startBubbleDecay(world, pos, range, length)
    }
}
