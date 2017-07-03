package com.cout970.magneticraft.item.itemblock

import com.cout970.magneticraft.block.ElectricMachines
import com.cout970.magneticraft.block.core.BlockBase
import com.cout970.magneticraft.misc.block.get
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.init.Blocks
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/07/03.
 */

class ItemBlockElectricPoleTransformer(blockBase: BlockBase) : ItemBlockBase(blockBase) {

    override fun onItemUse(player: EntityPlayer, worldIn: World, pos: BlockPos, hand: EnumHand, facing: EnumFacing,
                           hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {

        val itemstack = player.getHeldItem(hand)
        if (itemstack.isEmpty) return EnumActionResult.FAIL

        val state = worldIn.getBlockState(pos)

        if (state.block != ElectricMachines.electric_pole) return EnumActionResult.PASS
        val orientation = state[ElectricMachines.PROPERTY_POLE_ORIENTATION] ?: return EnumActionResult.PASS
        val offset = orientation.offsetY

        val basePos = pos.offset(EnumFacing.UP, offset)
        val floorPos = basePos.offset(EnumFacing.DOWN, 4)

        val baseState = worldIn.getBlockState(basePos)[ElectricMachines.PROPERTY_POLE_ORIENTATION]
                        ?: return EnumActionResult.PASS

        //@formatter:off
        ElectricMachines.air = true
        worldIn.setBlockState(basePos, Blocks.AIR.defaultState)
        ElectricMachines.air = false

        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 0), ElectricMachines.PoleOrientation.DOWN_4.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 1), ElectricMachines.PoleOrientation.DOWN_3.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 2), ElectricMachines.PoleOrientation.DOWN_2.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 3), ElectricMachines.PoleOrientation.DOWN_1.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 4), baseState.getBlockState(blockBase))
        //@formatter:on
        itemstack.shrink(1)
        return EnumActionResult.SUCCESS
    }
}