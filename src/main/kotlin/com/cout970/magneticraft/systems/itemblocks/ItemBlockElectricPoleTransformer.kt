package com.cout970.magneticraft.systems.itemblocks

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.systems.blocks.BlockBase
import net.minecraft.block.Blocks
import net.minecraft.item.ItemUseContext
import net.minecraft.util.ActionResultType
import com.cout970.magneticraft.features.electric_conductors.Blocks as ConductorBlocks

/**
 * Created by cout970 on 2017/07/03.
 */

class ItemBlockElectricPoleTransformer(blockBase: BlockBase) : ItemBlockBase(blockBase) {

    override fun onItemUse(ctx: ItemUseContext): ActionResultType {
        val player = ctx.player ?: return ActionResultType.PASS
        val itemStack = player.getHeldItem(ctx.hand)
        if (itemStack.isEmpty) return ActionResultType.FAIL

        val state = ctx.world.getBlockState(ctx.pos)

        if (state.block != ConductorBlocks.electricPole) return ActionResultType.PASS
        val orientation = state[ConductorBlocks.PROPERTY_POLE_ORIENTATION] ?: return ActionResultType.PASS
        val offset = orientation.offsetY

        val basePos = ctx.pos.offset(EnumFacing.UP, offset)
        val floorPos = basePos.offset(EnumFacing.DOWN, 4)

        val baseState = ctx.world.getBlockState(basePos)[ConductorBlocks.PROPERTY_POLE_ORIENTATION]
            ?: return ActionResultType.PASS

        //@formatter:off
        ConductorBlocks.air = true
        ctx.world.setBlockState(basePos, Blocks.AIR.defaultState)
        ConductorBlocks.air = false

        ctx.world.setBlockState(floorPos.offset(EnumFacing.UP, 0), ConductorBlocks.PoleOrientation.DOWN_4.getBlockState(blockBase))
        ctx.world.setBlockState(floorPos.offset(EnumFacing.UP, 1), ConductorBlocks.PoleOrientation.DOWN_3.getBlockState(blockBase))
        ctx.world.setBlockState(floorPos.offset(EnumFacing.UP, 2), ConductorBlocks.PoleOrientation.DOWN_2.getBlockState(blockBase))
        ctx.world.setBlockState(floorPos.offset(EnumFacing.UP, 3), ConductorBlocks.PoleOrientation.DOWN_1.getBlockState(blockBase))
        ctx.world.setBlockState(floorPos.offset(EnumFacing.UP, 4), baseState.getBlockState(blockBase))
        //@formatter:on
        itemStack.shrink(1)
        return ActionResultType.SUCCESS
    }

}