package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.block.BlockAirBubble
import com.cout970.magneticraft.block.BlockAirLock
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.TileBase
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.block.Block
import net.minecraft.init.Blocks

/**
 * Created by cout970 on 18/08/2016.
 */
@TileRegister("airlock")
class TileAirLock : TileBase() {

    val node = ElectricNode({ worldObj }, { pos }, capacity = 2.0)
    val traitElectricity = TraitElectricity(this, listOf(node))

    override val traits: List<ITileTrait> = listOf(traitElectricity)

    val range = 10
    val length = 9 * 9

    override fun update() {
        super.update()
        if (worldObj.isServer && shouldTick(40)) {
            Config.airlockBubbleCost = 2.0
            if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                val array = Array(range * 2 + 1, { Array(range * 2 + 1, { Array<Block?>(range * 2 + 1, { null }) }) })
                //fin water
                for (j in -range..range) {
                    for (k in -range..range) {
                        for (i in -range..range) {
                            val pos = pos.add(i, j, k)
                            var block = worldObj.getBlockState(pos).block
                            if (i * i + j * j + k * k <= length) {
                                if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                                    block = BlockAirBubble
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
                                if (array[x][y][z] == BlockAirBubble) {
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
                                if (array[x][y][z] == BlockAirBubble) {
                                    if (node.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE) {
                                        worldObj.setBlockState(pos.add(i, j, k), BlockAirBubble.defaultState.withProperty(BlockAirBubble.PROPERTY_DECAY, false))
                                        node.applyPower(-Config.airlockBubbleCost, false)
                                    } else {
                                        if (worldObj.getBlockState(pos.add(i, j, k)).block == BlockAirBubble) {
                                            node.applyPower(-Config.airlockBubbleCost, false)
                                        }
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
                                            worldObj.setBlockToAir(pos.add(i, j, k))
                                            node.applyPower(-Config.airlockAirCost, false)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                BlockAirLock.startBubbleDecay(worldObj, pos, range, length)
            }
        }
    }
}