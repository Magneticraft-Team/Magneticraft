@file:Suppress("unused", "NOTHING_TO_INLINE")

package com.cout970.magneticraft.misc

import com.cout970.magneticraft.*
import com.cout970.magneticraft.misc.vector.xd
import com.cout970.magneticraft.misc.vector.yd
import com.cout970.magneticraft.misc.vector.zd
import net.minecraft.item.ItemStack
import net.minecraft.nbt.*
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec3d
import net.minecraftforge.common.util.Constants
import java.util.*

/**
 * Created by cout970 on 17/07/2016.
 */
fun ItemStack.checkNBT(): NBTTagCompound {
    if (this.tagCompound == null) {
        this.tagCompound = NBTTagCompound()
    }
    this.tag
    return this.tagCompound!!
}

fun ItemStack.hasKey(key: String): Boolean {
    checkNBT()
    return tagCompound!!.hasKey(key)
}

fun ItemStack.setString(key: String, value: String) {
    checkNBT()
    return tagCompound!!.putString(key, value)
}

fun ItemStack.getString(key: String): String {
    checkNBT()
    return tagCompound!!.getString(key)
}

fun ItemStack.setDouble(key: String, value: Double) {
    checkNBT()
    return tagCompound!!.putDouble(key, value)
}

fun ItemStack.getDouble(key: String): Double {
    checkNBT()
    return tagCompound!!.getDouble(key)
}

fun ItemStack.setLore(values: List<String>) {
    val list = NBTTagList()
    val tag = NBTTagCompound()
    values.forEach {
        list.add(StringNBT(it))
    }
    tag.put("Lore", list)
    this.setTagInfo("display", tag)
}

fun ItemStack.setInteger(key: String, value: Int) {
    checkNBT()
    return tagCompound!!.putInt(key, value)
}

fun ItemStack.getInteger(key: String): Int {
    checkNBT()
    return tagCompound!!.getInt(key)
}

fun ItemStack.setBoolean(key: String, value: Boolean) {
    checkNBT()
    return tagCompound!!.putBoolean(key, value)
}

fun ItemStack.getBoolean(key: String): Boolean {
    checkNBT()
    return tagCompound!!.getBoolean(key)
}

fun ItemStack.setBlockPos(key: String, pos: BlockPos) {
    checkNBT()
    val tag = NBTTagCompound()
    tag.putInt("x", pos.x)
    tag.putInt("y", pos.y)
    tag.putInt("z", pos.z)
    tagCompound!!.put(key, tag)
}

fun ItemStack.getBlockPos(key: String): BlockPos {
    checkNBT()
    val tag = tagCompound!!.getCompoundTag(key)
    return BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"))
}

fun NBTTagCompound.setBlockPos(key: String, pos: BlockPos) = put(key, NBTTagList().apply {
    add(IntNBT(pos.x))
    add(IntNBT(pos.y))
    add(IntNBT(pos.z))
})

fun NBTTagCompound.getVector3(key: String): Vec3d {
    val list = getList(key, Constants.NBT.TAG_DOUBLE)
    return Vec3d(list.getDouble(0), list.getDouble(1), list.getDouble(2))
}

fun NBTTagCompound.setVector3(key: String, pos: Vec3d) = put(key, NBTTagList().apply {
    add(DoubleNBT(pos.xd))
    add(DoubleNBT(pos.yd))
    add(DoubleNBT(pos.zd))
})

fun NBTTagCompound.getBlockPos(key: String): BlockPos {
    val list = getList(key, Constants.NBT.TAG_INT)
    return BlockPos(list.getInt(0), list.getInt(1), list.getInt(2))
}

fun NBTTagCompound.setEnumFacing(key: String, facing: EnumFacing?) {
    putInt(key, facing?.ordinal ?: -1)
}

fun NBTTagCompound.getEnumFacing(key: String): EnumFacing = EnumFacing.byIndex(getInteger(key))
fun NBTTagCompound.getNullableEnumFacing(key: String): EnumFacing? {
    return if (getInteger(key) < 0) null else EnumFacing.byIndex(getInteger(key))
}

// Builders

fun newNbt(func: NBTTagCompound.() -> Unit): NBTTagCompound {
    val nbt = NBTTagCompound()
    func(nbt)
    return nbt
}

fun NBTTagList.newNbt(func: NBTTagCompound.() -> Unit) {
    val nbt = NBTTagCompound()
    func(nbt)
    add(nbt)
}

fun NBTTagCompound.list(key: String, func: NBTTagList.() -> Unit) {
    val list = NBTTagList()
    func(list)
    put(key, list)
}

fun NBTTagCompound.getList(key: String): NBTTagList {
    return getList(key, Constants.NBT.TAG_COMPOUND)
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

inline fun NBTTagList.forEachTag(action: (NBTTagCompound) -> Unit) {
    for (i in 0 until count()) action(getCompound(i))
}

inline fun NBTTagCompound.forEach(action: (String, Any) -> Unit) {
    for (key in keySet()) {
        action(key, get(key)!!)
    }
}

fun NBTTagCompound.add(key: String, value: Int) = putInt(key, value)
fun NBTTagCompound.add(key: String, value: Boolean) = putBoolean(key, value)
fun NBTTagCompound.add(key: String, value: Float) = putFloat(key, value)
fun NBTTagCompound.add(key: String, value: Double) = putDouble(key, value)
fun NBTTagCompound.add(key: String, value: Byte) = putByte(key, value)
fun NBTTagCompound.add(key: String, value: Short) = putShort(key, value)
fun NBTTagCompound.add(key: String, value: Long) = putLong(key, value)
fun NBTTagCompound.add(key: String, value: UUID) = putUniqueId(key, value)
fun NBTTagCompound.add(key: String, value: BlockPos) = setBlockPos(key, value)
fun NBTTagCompound.add(key: String, value: Vec3d) = setVector3(key, value)
fun NBTTagCompound.add(key: String, value: IntArray) = putIntArray(key, value)
fun NBTTagCompound.add(key: String, value: String) = putString(key, value)
fun NBTTagCompound.add(key: String, value: NBTTagCompound) = put(key, value)
fun NBTTagCompound.add(key: String, value: EnumFacing?) = setEnumFacing(key, value)

inline operator fun NBTTagCompound.set(key: String, value: Int) = putInt(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Boolean) = putBoolean(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Float) = putFloat(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Double) = putDouble(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Byte) = putByte(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Short) = putShort(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Long) = putLong(key, value)
inline operator fun NBTTagCompound.set(key: String, value: UUID) = putUniqueId(key, value)
inline operator fun NBTTagCompound.set(key: String, value: BlockPos) = setBlockPos(key, value)
inline operator fun NBTTagCompound.set(key: String, value: Vec3d) = setVector3(key, value)
inline operator fun NBTTagCompound.set(key: String, value: IntArray) = putIntArray(key, value)
inline operator fun NBTTagCompound.set(key: String, value: String) = putString(key, value)
inline operator fun NBTTagCompound.set(key: String, value: INBT) = put(key, value)
inline operator fun NBTTagCompound.set(key: String, value: EnumFacing?) = setEnumFacing(key, value)


fun NBTTagCompound.toMap(): Map<String, Any> {
    val map = mutableMapOf<String, Any>()
    forEach { key, value ->
        map[key] = when (value) {
            is NBTTagCompound -> value.toMap()
            is FloatNBT -> value.float
            is DoubleNBT -> value.double
            is LongNBT -> value.long
            is IntNBT -> value.int
            is ShortNBT -> value.short
            is ByteNBT -> value.byte
            is IntArrayNBT -> value.intArray
            is ByteArrayNBT -> value.byteArray
            else -> error("Unknown type: class = ${value::class.java}, value = $value")
        }
    }
    return map
}

@Suppress("UNCHECKED_CAST")
fun Map<String, Any>.toNBT(): NBTTagCompound {
    val nbt = NBTTagCompound()

    forEach { key, value ->
        when (value) {
            is Float -> nbt.putFloat(key, value)
            is Double -> nbt.putDouble(key, value)
            is Int -> nbt.putInt(key, value)
            is Long -> nbt.putLong(key, value)
            is Short -> nbt.putShort(key, value)
            is Byte -> nbt.putByte(key, value)
            is Boolean -> nbt.putBoolean(key, value)
            is IntArray -> nbt.putIntArray(key, value)
            is ByteArray -> nbt.putByteArray(key, value)
            is Map<*, *> -> nbt.put(key, (value as Map<String, Any>).toNBT())
            else -> error("Unknown type: class = ${value::class.java}, value = $value")
        }
    }
    return nbt
}