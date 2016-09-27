package com.cout970.magneticraft.util

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import io.netty.buffer.ByteBuf
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagInt
import net.minecraft.nbt.NBTTagList
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.TextFormatting
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants

/**
 * Created by cout970 on 29/06/2016.
 */

fun TileEntity.shouldTick(time: Int): Boolean {
    return (world.totalWorldTime + pos.hashCode()) % time == 0L
}

operator fun <T : Comparable<T>> IProperty<T>.get(state: IBlockState): T = state.getValue(this)

fun <T : Comparable<T>> IProperty<T>.isIn(state: IBlockState): Boolean = this in state.properties

operator fun IElectricConnection.contains(node: IElectricNode) = this.firstNode == node || this.secondNode == node

fun ByteBuf.readString(): String {
    val size = Math.abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return kotlin.text.String(buffer, charset = Charsets.UTF_8)
}

fun ByteBuf.writeString(str: String) {
    val array = str.toByteArray(Charsets.UTF_8)
    this.writeShort(array.size)
    for (i in 0 until array.size) {
        this.writeByte(array[i].toInt())
    }
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any) {
    addChatComponentMessage(TextComponentTranslation(str, *args))
}

fun EntityPlayer.sendMessage(str: String, vararg args: Any, color: TextFormatting) {
    addChatComponentMessage(TextComponentTranslation(str, *args).apply { style.color = color })
}

fun translate(str: String, vararg args: Any) = TextComponentTranslation(str, *args)

val World.isServer: Boolean get() = !isRemote
val World.isClient: Boolean get() = isRemote

fun NBTTagCompound.setBlockPos(key: String, pos: BlockPos) = setTag(key, NBTTagList().apply {
    appendTag(NBTTagInt(pos.x))
    appendTag(NBTTagInt(pos.y))
    appendTag(NBTTagInt(pos.z))
})

fun NBTTagCompound.getBlockPos(key: String): BlockPos {
    val list = getTagList(key, Constants.NBT.TAG_INT)
    return BlockPos(list.getIntAt(0), list.getIntAt(1), list.getIntAt(2))
}

fun NBTTagCompound.setEnumFacing(key: String, facing: EnumFacing) {
    setInteger(key, facing.ordinal)
}

fun NBTTagCompound.getEnumFacing(key: String) = EnumFacing.getFront(getInteger(key))!!

fun AxisAlignedBB.cut(other: AxisAlignedBB): AxisAlignedBB? {
    if (!this.intersectsWith(other)) return null
    return AxisAlignedBB(
            Math.max(minX, other.minX), Math.max(minY, other.minY), Math.max(minZ, other.minZ),
            Math.min(maxX, other.maxX), Math.min(maxY, other.maxY), Math.min(maxZ, other.maxZ))
}

val EMPTY_AABB = AxisAlignedBB(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)