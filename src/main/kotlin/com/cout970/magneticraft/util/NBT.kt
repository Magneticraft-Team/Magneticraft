@file:Suppress("unused")

package com.cout970.magneticraft.util

import com.cout970.magneticraft.util.vector.xd
import com.cout970.magneticraft.util.vector.yd
import com.cout970.magneticraft.util.vector.zd
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.util.Constants
import java.util.*

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
    appendTag(NBTTagDouble(pos.xd))
    appendTag(NBTTagDouble(pos.yd))
    appendTag(NBTTagDouble(pos.zd))
})

fun NBTTagCompound.getBlockPos(key: String): BlockPos {
    val list = getTagList(key, Constants.NBT.TAG_INT)
    return BlockPos(list.getIntAt(0), list.getIntAt(1), list.getIntAt(2))
}

fun NBTTagCompound.setEnumFacing(key: String, facing: EnumFacing) {
    setInteger(key, facing.ordinal)
}

fun NBTTagCompound.getEnumFacing(key: String) = EnumFacing.getFront(getInteger(key))!!

// Builders

fun newNbt(func: NBTTagCompound.() -> Unit): NBTTagCompound {
    val nbt = NBTTagCompound()
    func(nbt)
    return nbt
}

fun NBTTagList.newNbt(func: NBTTagCompound.() -> Unit) {
    val nbt = NBTTagCompound()
    func(nbt)
    appendTag(nbt)
}

fun NBTTagCompound.list(key: String, func: NBTTagList.() -> Unit) {
    val list = NBTTagList()
    func(list)
    setTag(key, list)
}

fun NBTTagCompound.getList(key: String): NBTTagList {
    return getTagList(key, Constants.NBT.TAG_COMPOUND)
}

fun NBTTagList.forEach(func: (NBTTagCompound) -> Unit) {
    for (i in 0 until tagCount()) {
        func(getCompoundTagAt(i))
    }
}

fun NBTTagCompound.readList(key: String, func: (NBTTagList) -> Unit) {
    if (hasKey(key)) {
        try {
            func(getList(key))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun NBTTagList.getTagCompound(index: Int) = getCompoundTagAt(index)!!

fun NBTTagCompound.add(key: String, value: Int) = setInteger(key, value)
fun NBTTagCompound.add(key: String, value: Float) = setFloat(key, value)
fun NBTTagCompound.add(key: String, value: Double) = setDouble(key, value)
fun NBTTagCompound.add(key: String, value: Byte) = setByte(key, value)
fun NBTTagCompound.add(key: String, value: Short) = setShort(key, value)
fun NBTTagCompound.add(key: String, value: Long) = setLong(key, value)
fun NBTTagCompound.add(key: String, value: UUID) = setUniqueId(key, value)
fun NBTTagCompound.add(key: String, value: EnumFacing) = setEnumFacing(key, value)
fun NBTTagCompound.add(key: String, value: BlockPos) = setBlockPos(key, value)
fun NBTTagCompound.add(key: String, value: Vec3d) = setVector3(key, value)
fun NBTTagCompound.add(key: String, value: IntArray) = setIntArray(key, value)
fun NBTTagCompound.add(key: String, value: String) = setString(key, value)
fun NBTTagCompound.add(key: String, value: NBTTagCompound) = setTag(key, value)
