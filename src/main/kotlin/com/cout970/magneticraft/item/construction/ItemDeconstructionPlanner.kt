package com.cout970.magneticraft.item.construction

import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.util.vector.plus
import com.cout970.magneticraft.util.vector.rotateBox
import com.cout970.magneticraft.util.vector.toVec3d
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World


/**
 * Created by Yurgen on 01/12/2016.
 */

//TODO add to game or remove
@Suppress("unused")
class ItemDeconstructionPlanner : ItemMod("deconstruction_planner") {
    var designations: MutableSet<BlockPos> = mutableSetOf()

    var width = 1.0
    var depth = 1.0
    var height = 1.0

    override fun getItemStackLimit(stack: ItemStack?): Int = 1

    override fun updateItemStackNBT(nbt: NBTTagCompound?): Boolean {
        if (nbt == null) return super.updateItemStackNBT(nbt)
        val xArray = IntArray(designations.size)
        val yArray = IntArray(designations.size)
        val zArray = IntArray(designations.size)
        designations.forEachIndexed { i, blockPos ->
            xArray.set(i, blockPos.x)
            yArray.set(i, blockPos.y)
            zArray.set(i, blockPos.z)
        }
        nbt.setIntArray("xcoords", xArray)
        nbt.setIntArray("ycoords", yArray)
        nbt.setIntArray("zcoords", zArray)
        return super.updateItemStackNBT(nbt)
    }

    override fun onUpdate(stack: ItemStack?, worldIn: World?, entityIn: Entity?, itemSlot: Int, isSelected: Boolean) {
        if (stack == null) return super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
        if (stack.tagCompound == null) {
            stack.tagCompound = NBTTagCompound()
        }
        if (worldIn == null) return super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)
        if (worldIn.isServer) {
            val block = worldIn.getBlockState(designations.first()) ?: return super.onUpdate(stack, worldIn, entityIn, itemSlot, isSelected)

        }
    }

    override fun onItemUse(stack: ItemStack?, playerIn: EntityPlayer?, worldIn: World?, pos: BlockPos?, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (pos == null || playerIn == null || worldIn == null || facing == null) return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
        val box = facing.rotateBox(pos.toVec3d(), (AxisAlignedBB(-width, -height, 0.0, width, height, depth))) + pos.toVec3d()
        for (i in box.minX.toInt() until box.maxX.toInt()) {
            for (j in box.minY.toInt() until box.maxY.toInt()) {
                for (k in box.minZ.toInt() until box.maxZ.toInt()) {
                    designations.add(pos.add(i, j, k))
                }
            }
        }
        return super.onItemUse(stack, playerIn, worldIn, pos, hand, facing, hitX, hitY, hitZ)
    }


}