package com.cout970.magneticraft.item.itemblock

import com.cout970.magneticraft.block.ElectricConductors
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

        if (state.block != ElectricConductors.electric_pole) return EnumActionResult.PASS
        val orientation = state[ElectricConductors.PROPERTY_POLE_ORIENTATION] ?: return EnumActionResult.PASS
        val offset = orientation.offsetY

        val basePos = pos.offset(EnumFacing.UP, offset)
        val floorPos = basePos.offset(EnumFacing.DOWN, 4)

        val baseState = worldIn.getBlockState(basePos)[ElectricConductors.PROPERTY_POLE_ORIENTATION]
                        ?: return EnumActionResult.PASS

        //@formatter:off
        ElectricConductors.air = true
        worldIn.setBlockState(basePos, Blocks.AIR.defaultState)
        ElectricConductors.air = false

        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 0), ElectricConductors.PoleOrientation.DOWN_4.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 1), ElectricConductors.PoleOrientation.DOWN_3.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 2), ElectricConductors.PoleOrientation.DOWN_2.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 3), ElectricConductors.PoleOrientation.DOWN_1.getBlockState(blockBase))
        worldIn.setBlockState(floorPos.offset(EnumFacing.UP, 4), baseState.getBlockState(blockBase))
        //@formatter:on
        itemstack.shrink(1)
        return EnumActionResult.SUCCESS
    }
}