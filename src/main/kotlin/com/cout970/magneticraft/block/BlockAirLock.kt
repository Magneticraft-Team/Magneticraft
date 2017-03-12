package com.cout970.magneticraft.block

import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.tileentity.electric.TileAirLock
import com.teamwizardry.librarianlib.common.base.block.BlockMod
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 18/08/2016.
 */
object BlockAirLock : BlockModContainer("airlock", Material.IRON) {

    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileAirLock()

    override fun breakBlock(worldIn: World, pos: BlockPos, state: IBlockState) {
        val tile = worldIn.getTileEntity(pos)
        if (tile is TileAirLock) {
            startBubbleDecay(worldIn, pos, tile.range, tile.length)
        }
        super.breakBlock(worldIn, pos, state)
    }

    fun startBubbleDecay(world: World, pos: BlockPos, range: Int, length: Int) {
        for (j in -range..range) {
            for (k in -range..range) {
                for (i in -range..range) {
                    if (i * i + j * j + k * k <= length) {
                        val state = world.getBlockState(pos.add(i, j, k))
                        if (state.block == BlockAirBubble) {
                            if (!state[BlockAirBubble.PROPERTY_DECAY]) {
                                world.setBlockState(pos.add(i, j, k), state.withProperty(BlockAirBubble.PROPERTY_DECAY, true))
                                world.scheduleUpdate(pos.add(i, j, k), this, 0)
                            }
                        }
                    }
                }
            }
        }
    }
}