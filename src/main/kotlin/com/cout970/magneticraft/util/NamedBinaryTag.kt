package com.cout970.magneticraft.util

import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 17/07/2016.
 */

fun ItemStack.checkNBT() {
    if (this.tagCompound == null) {
        this.tagCompound = NBTTagCompound()
    }
}

fun ItemStack.hasKey(key:String):Boolean {
    checkNBT()
    return tagCompound!!.hasKey(key)
}

fun ItemStack.setString(key: String, value:String){
    checkNBT()
    return tagCompound!!.setString(key, value)
}

fun ItemStack.getString(key: String): String{
    checkNBT()
    return tagCompound!!.getString(key)
}

fun ItemStack.setDouble(key: String, value:Double){
    checkNBT()
    return tagCompound!!.setDouble(key, value)
}

fun ItemStack.getDouble(key: String): Double{
    checkNBT()
    return tagCompound!!.getDouble(key)
}

fun ItemStack.setBlockPos(key: String, pos: BlockPos) {
    checkNBT()
    val tag = NBTTagCompound()
    tag.setInteger("x", pos.x)
    tag.setInteger("y", pos.y)
    tag.setInteger("z", pos.z)
    tagCompound!!.setTag(key, tag)
}

fun ItemStack.getBlockPos(key: String): BlockPos {
    checkNBT()
    val tag = tagCompound!!.getCompoundTag(key)
    return BlockPos(tag.getInteger("x"), tag.getInteger("y"), tag.getInteger("z"))
}