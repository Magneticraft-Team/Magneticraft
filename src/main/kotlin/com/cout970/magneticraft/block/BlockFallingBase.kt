package com.cout970.magneticraft.block

import com.cout970.magneticraft.misc.world.isServer
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.item.EntityFallingBlock
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.*

/**
 * Created by Yurgen on 18/10/2016.
 */

open class BlockFallingBase(
        registryName: String,
        unlocalizedName: String = registryName) :
        BlockMod(
                registryName,
                Material.SAND,
                unlocalizedName) {

    /**
     * Called after the block is set in the Chunk data, but before the Tile Entity is set
     */
    override fun onBlockAdded(worldIn: World, pos: BlockPos, state: IBlockState) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    /**
     * Called when a neighboring block was changed and marks that this state should perform any checks during a neighbor
     * change. Cases may include when redstone power is updated, cactus blocks popping off due to a neighboring solid
     * block, etc.
     */
    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn))
    }

    override fun updateTick(worldIn: World, pos: BlockPos, state: IBlockState, rand: Random) {
        if (worldIn.isServer) {
            this.checkFallable(worldIn, pos)
        }
    }

    private fun checkFallable(worldIn: World, pos: BlockPos) {
        if ((worldIn.isAirBlock(pos.down()) || canFallThrough(worldIn.getBlockState(pos.down()))) && pos.y >= 0) {
            val i = 32

            if (!fallInstantly && worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                if (worldIn.isServer) {
                    val entityfallingblock = EntityFallingBlock(worldIn, pos.x.toDouble() + 0.5, pos.y.toDouble(),
                            pos.z.toDouble() + 0.5, worldIn.getBlockState(pos))
                    this.onStartFalling(entityfallingblock)
                    worldIn.spawnEntityInWorld(entityfallingblock)
                }
            } else {
                worldIn.setBlockToAir(pos)
                var blockpos: BlockPos

                blockpos = pos.down()
                while ((worldIn.isAirBlock(blockpos) || canFallThrough(
                        worldIn.getBlockState(blockpos))) && blockpos.y > 0) {
                    blockpos = blockpos.down()
                }

                if (blockpos.y > 0) {
                    worldIn.setBlockState(blockpos.up(), this.defaultState)
                }
            }
        }
    }

    protected open fun onStartFalling(fallingEntity: EntityFallingBlock) {
    }

    /**
     * How many world ticks before ticking
     */
    override fun tickRate(worldIn: World): Int {
        return 2
    }

    companion object {
        var fallInstantly: Boolean = false

        fun canFallThrough(state: IBlockState): Boolean {
            val block = state.block
            val material = state.material
            return block === Blocks.FIRE || material === Material.AIR || material === Material.WATER || material === Material.LAVA
        }
    }
}