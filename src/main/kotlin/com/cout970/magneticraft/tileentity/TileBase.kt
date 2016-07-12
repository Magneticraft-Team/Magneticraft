package com.cout970.magneticraft.tileentity

import net.minecraft.entity.item.EntityItem
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos

abstract class TileBase : TileEntity() {

    open fun onBreak() {
    }

    fun dropItem(last: ItemStack, pos: BlockPos) {
        if (!world.isRemote) {
            val f = 0.05f
            val d0 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d1 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val d2 = (world.rand.nextFloat() * f).toDouble() + (1.0f - f).toDouble() * 0.5
            val entityItem = EntityItem(world, pos.x.toDouble() + d0, pos.y.toDouble() + d1, pos.z.toDouble() + d2, last)
            entityItem.setDefaultPickupDelay()
            world.spawnEntityInWorld(entityItem)
        }
    }

    override fun readFromNBT(compound: NBTTagCompound?) {
        if (compound!!.hasKey("TileData")) {
            load(compound.getCompoundTag("TileData"))
        }
        super.readFromNBT(compound)
    }

    override fun writeToNBT(compound: NBTTagCompound?): NBTTagCompound? {
        compound?.apply { setTag("TileData", save()) }
        return super.writeToNBT(compound)
    }

    override fun getMaxRenderDistanceSquared(): Double = 128.0 * 128.0

    abstract fun save(): NBTTagCompound
    abstract fun load(nbt: NBTTagCompound)
}