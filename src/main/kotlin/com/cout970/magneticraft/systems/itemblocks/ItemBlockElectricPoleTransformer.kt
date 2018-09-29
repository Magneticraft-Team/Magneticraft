package com.cout970.magneticraft.systems.itemblocks

import com.cout970.magneticraft.misc.block.get
import com.cout970.magneticraft.systems.blocks.BlockBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import com.cout970.magneticraft.features.electric_conductors.Blocks as ConductorBlocks

/**
 * Created by cout970 on 2017/07/03.
 */

class ItemBlockElectricPoleTransformer(blockBase: BlockBase) : ItemBlockBase(blockBase) {

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing,
                           hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        val itemStack = player.getHeldItem(hand)
        if (itemStack.isEmpty) return EnumActionResult.FAIL

        val state = worldIn.getBlockState(pos)

        if (state.block != ConductorBlocks.electricPole) return EnumActionResult.PASS
        val orientation = state[ConductorBlocks.PROPERTY_POLE_ORIENTATION] ?: return EnumActionResult.PASS
        val offset = orientation.offsetY

        val basePos = pos.offset(EnumFacing.UP, offset)
        val floorPos = basePos.offset(EnumFacing.DOWN, 4)

        val baseState = worldIn.getBlockState(basePos)[ConductorBlocks.PROPERTY_POLE_ORIENTATION]
            ?: return EnumActionResult.PASS

        //@formatter:off
        ConductorBlocks.air = true
        worldIn.setBlockState(basePos, Blocks.AIR.defaultState)
        ConductorBlocks.air = false

        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 0), ConductorBlocks.PoleOrientation.DOWN_4.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 1), ConductorBlocks.PoleOrientation.DOWN_3.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 2), ConductorBlocks.PoleOrientation.DOWN_2.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 3), ConductorBlocks.PoleOrientation.DOWN_1.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 4), baseState.getBlockState(blockBase))
        //@formatter:on
        itemStack.shrink(1)
        return EnumActionResult.SUCCESS
    }
}