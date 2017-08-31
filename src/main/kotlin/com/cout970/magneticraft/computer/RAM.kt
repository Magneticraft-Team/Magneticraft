package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IMemory
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2016/09/30.
 */
class RAM(val size: Int, val littleEndian: Boolean = true) : IMemory {

    val mem = ByteArray(size)

    override fun readByte(addr: Int): Byte {
        if (addr < 0 || addr >= memorySize)
            return 0
        return mem[addr]
    }

    override fun writeByte(addr: Int, data: Byte) {
        if (addr < 0 || addr >= memorySize) return
        mem[addr] = data
    }

    override fun isLittleEndian(): Boolean = littleEndian

    override fun getMemorySize(): Int = size

    override fun writeWord(pos: Int, data: Int) {
        if (!littleEndian) {
            writeByte(pos + 3, (data and 0x000000FF).toByte())
            writeByte(pos + 2, (data and 0x0000FF00 shr 8).toByte())
            writeByte(pos + 1, (data and 0x00FF0000 shr 16).toByte())
            writeByte(pos, (data and 0xFF000000.toInt() shr 24).toByte())
        } else {
            writeByte(pos, (data and 0x000000FF).toByte())
            writeByte(pos + 1, (data and 0x0000FF00 shr 8).toByte())
            writeByte(pos + 2, (data and 0x00FF0000 shr 16).toByte())
            writeByte(pos + 3, (data and 0xFF000000.toInt() shr 24).toByte())
        }
    }

    override fun readWord(pos: Int): Int {
        var data: Int
        if (littleEndian) {
            data = readByte(pos + 3).toInt() and 0xFF
            data = data or (readByte(pos + 2).toInt() and 0xFF shl 8)
            data = data or (readByte(pos + 1).toInt() and 0xFF shl 16)
            data = data or (readByte(pos).toInt() and 0xFF shl 24)
        } else {
            data = readByte(pos).toInt() and 0xFF
            data = data or (readByte(pos + 1).toInt() and 0xFF shl 8)
            data = data or (readByte(pos + 2).toInt() and 0xFF shl 16)
            data = data or (readByte(pos + 3).toInt() and 0xFF shl 24)
        }
        return data
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        System.arraycopy(nbt!!.getByteArray("mem"), 0,  mem, 0, size)
    }

    override fun serializeNBT(): NBTTagCompound = NBTTagCompound().apply { setByteArray("mem", mem) }
}