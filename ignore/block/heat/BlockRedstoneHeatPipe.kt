@file:Suppress("DEPRECATION", "OverridingDeprecatedMember")

package com.cout970.magneticraft.block.heat


import com.cout970.magneticraft.block.PROPERTY_OPEN
import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.misc.tileentity.getTile
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.heat.TileRedstoneHeatPipe
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import com.teamwizardry.librarianlib.common.base.block.BlockModContainer
import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 04/07/2016.
 */
object BlockRedstoneHeatPipe : BlockModContainer("redstone_heat_pipe", Material.ROCK) {

    init {
        tickRandomly = true
    }
    override fun createTileEntity(worldIn: World, meta: IBlockState): TileEntity = TileRedstoneHeatPipe()

    override fun onBlockPlacedBy(worldIn: World?, pos: BlockPos, state: IBlockState?, placer: EntityLivingBase, stack: ItemStack?) {
        super.onBlockPlacedBy(worldIn, pos, state, placer, stack)
        worldIn?.setBlockState(pos, defaultState.withProperty(PROPERTY_OPEN, false))
    }

    override fun getMetaFromState(state: IBlockState): Int {
        if (state[PROPERTY_OPEN]) return 1 else return 0
    }

    override fun getStateFromMeta(meta: Int): IBlockState = defaultState.
            withProperty(PROPERTY_OPEN, (meta and 1) != 0)

    override fun createBlockState(): BlockStateContainer {
        return BlockStateContainer(this, PROPERTY_OPEN)
    }

    override fun neighborChanged(state: IBlockState, worldIn: World, pos: BlockPos, blockIn: Block) {
        if (worldIn.isServer) {
            val flag = worldIn.isBlockPowered(pos)
            if (flag || blockIn.defaultState.canProvidePower()) {
                if (state.get(PROPERTY_OPEN) != flag) {
                    worldIn.setBlockState(pos, defaultState.withProperty(PROPERTY_OPEN, flag))
                    val tile = worldIn.getTile<TileRedstoneHeatPipe>(pos)
                    tile?.heat?.conductivity = if (flag) 0.0 else DEFAULT_CONDUCTIVITY
                }
            }
        }
        super.neighborChanged(state, worldIn, pos, blockIn)
    }
}