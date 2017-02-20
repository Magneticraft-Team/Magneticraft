package com.cout970.magneticraft.misc.network

import io.netty.buffer.ByteBuf
import java.util.*

/**
 * Created by cout970 on 2017/02/20.
 */

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

fun ByteBuf.readByteArray(): ByteArray {
    val size = Math.abs(this.readShort().toInt())
    val buffer = ByteArray(size)
    for (i in 0 until size) {
        buffer[i] = this.readByte()
    }
    return buffer
}

fun ByteBuf.writeByteArray(array: ByteArray) {
    this.writeShort(array.size)
    for (i in 0 until array.size) {
        this.writeByte(array[i].toInt())
    }
}

fun ByteBuf.writeUUID(uuid: UUID) {
    this.writeLong(uuid.mostSignificantBits)
    this.writeLong(uuid.leastSignificantBits)
}

fun ByteBuf.readUUID(): UUID {
    return UUID(this.readLong(), this.readLong())
}