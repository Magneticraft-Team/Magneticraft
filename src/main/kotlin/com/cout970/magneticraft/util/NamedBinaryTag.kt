package com.cout970.magneticraft.util

import coffee.cypher.mcextlib.extensions.vectors.x
import coffee.cypher.mcextlib.extensions.vectors.y
import coffee.cypher.mcextlib.extensions.vectors.z
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.util.Constants

/**
 * Created by cout970 on 17/07/2016.
 */
fun ItemStack.checkNBT() {
    if (this.tagCompound == null) {
        this.tagCompound = NBTTagCompound()
    }
}

fun ItemStack.hasKey(key: String): Boolean {
    checkNBT()
    return tagCompound!!.hasKey(key)
}

fun ItemStack.setString(key: String, value: String) {
    checkNBT()
    return tagCompound!!.setString(key, value)
}

fun ItemStack.getString(key: String): String {
    checkNBT()
    return tagCompound!!.getString(key)
}

fun ItemStack.setDouble(key: String, value: Double) {
    checkNBT()
    return tagCompound!!.setDouble(key, value)
}

fun ItemStack.getDouble(key: String): Double {
    checkNBT()
    return tagCompound!!.getDouble(key)
}

fun ItemStack.setLore(values: List<String>) {
    val list = NBTTagList()
    val tag = NBTTagCompound()
    values.forEach {
        list.appendTag(NBTTagString(it))
    }
    tag.setTag("Lore", list)
    this.setTagInfo("display", tag)
}

fun ItemStack.setInteger(key: String, value: Int) {
    checkNBT()
    return tagCompound!!.setInteger(key, value)
}

fun ItemStack.getInteger(key: String): Int {
    checkNBT()
    return tagCompound!!.getInteger(key)
}

fun ItemStack.setBoolean(key: String, value: Boolean) {
    checkNBT()
    return tagCompound!!.setBoolean(key, value)
}

fun ItemStack.getBoolean(key: String): Boolean {
    checkNBT()
    return tagCompound!!.getBoolean(key)
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

fun NBTTagCompound.setBlockPos(key: String, pos: BlockPos) = setTag(key, NBTTagList().apply {
    appendTag(NBTTagInt(pos.x))
    appendTag(NBTTagInt(pos.y))
    appendTag(NBTTagInt(pos.z))
})

fun NBTTagCompound.getVector3(key: String): Vec3d {
    val list = getTagList(key, Constants.NBT.TAG_DOUBLE)
    return Vec3d(list.getDoubleAt(0), list.getDoubleAt(1), list.getDoubleAt(2))
}

fun NBTTagCompound.setVector3(key: String, pos: Vec3d) = setTag(key, NBTTagList().apply {
    appendTag(NBTTagDouble(pos.x))
    appendTag(NBTTagDouble(pos.y))
    appendTag(NBTTagDouble(pos.z))
})

fun NBTTagCompound.getBlockPos(key: String): BlockPos {
    val list = getTagList(key, Constants.NBT.TAG_INT)
    return BlockPos(list.getIntAt(0), list.getIntAt(1), list.getIntAt(2))
}

fun NBTTagCompound.setEnumFacing(key: String, facing: EnumFacing) {
    setInteger(key, facing.ordinal)
}

fun NBTTagCompound.getEnumFacing(key: String) = EnumFacing.getFront(getInteger(key))!!