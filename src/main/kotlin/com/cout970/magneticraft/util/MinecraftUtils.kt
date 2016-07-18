package com.cout970.magneticraft.util

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import io.netty.buffer.ByteBuf
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity

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
