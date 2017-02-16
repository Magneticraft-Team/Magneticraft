package com.cout970.magneticraft.util

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import java.util.*

/**
 * Created by cout970 on 2017/02/16.
 */
fun nbt(func: NBTTagCompound.() -> Unit): NBTTagCompound {
    val nbt = NBTTagCompound()
    func(nbt)
    return nbt
}

fun NBTTagList.nbt(func: NBTTagCompound.() -> Unit) {
    val nbt = NBTTagCompound()
    func(nbt)
    appendTag(nbt)
}

fun NBTTagCompound.addList(key: String, func: NBTTagList.()->Unit){
    val list = NBTTagList()
    func(list)
    setTag(key, list)
}

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
