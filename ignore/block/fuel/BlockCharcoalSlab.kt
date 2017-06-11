package com.cout970.magneticraft.block.fuel

import com.teamwizardry.librarianlib.common.base.block.BlockModSlab
import net.minecraft.block.state.IBlockState
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import java.util.*

/**
 * Created by Yurgen on 08/11/2016.
 */

object BlockCharcoalSlab : BlockModSlab("charcoal_slab", BlockCoke.defaultState) {

    override fun getItemDropped(state: IBlockState, rand: Random, fortune: Int): Item? {
        return Items.COAL
    }
    override fun getFlammability(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?): Int = 5
    override fun getFireSpreadSpeed(world: IBlockAccess?, pos: BlockPos?, face: EnumFacing?): Int = 10

    override fun damageDropped(state: IBlockState): Int {
        return 1
    }

    override fun quantityDroppedWithBonus(fortune: Int, random: Random?): Int {
        if (random?.nextInt(4) ?: 3 < fortune) return 2
        else return 1
    }
}