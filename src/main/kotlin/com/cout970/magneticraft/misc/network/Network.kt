package com.cout970.magneticraft.misc.network

import io.netty.buffer.ByteBuf
import net.minecraft.util.math.BlockPos
import java.util.*
import kotlin.math.abs

/**
 * Created by cout970 on 2017/02/20.
 */

fun ByteBuf.readString(): String {
    val size = abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return String(buffer, charset = Charsets.UTF_8)
}

fun ByteBuf.writeString(str: String) {
    val array = str.toByteArray(Charsets.UTF_8)
    this.writeShort(array.size)
    for (element in array) {
        this.writeByte(element.toInt())
    }
}

fun ByteBuf.readByteArray(): ByteArray {
    val size = abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return buffer
}

fun ByteBuf.writeByteArray(array: ByteArray) {
    this.writeShort(array.size)
    for (element in array) {
        this.writeByte(element.toInt())
    }
}

fun ByteBuf.readIntArray(): IntArray {
    val size = abs(this.readShort().toInt())
    val buffer = IntArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readInt()
    }
    return buffer
}

fun ByteBuf.writeIntArray(array: IntArray) {
    this.writeShort(array.size)
    for (element in array) {
        this.writeInt(element)
    }
}

fun ByteBuf.readBlockPos(): BlockPos {
    return BlockPos(readInt(), readInt(), readInt())
}

fun ByteBuf.writeBlockPos(pos: BlockPos) {
    writeInt(pos.x)
    writeInt(pos.y)
    writeInt(pos.z)
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUUID(): UUID {
    return UUID(this.readLong(), this.readLong())
}