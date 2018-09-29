package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IRAM

/**
 * Created by cout970 on 2016/09/30.
 */
class RAM(val size: Int, val littleEndian: Boolean) : IRAM {

    val mem = ByteArray(size)

    override fun readByte(addr: Int): Byte {
        if (addr < 0 || addr >= memorySize) return 0
        return mem[addr]
    }

    override fun writeByte(addr: Int, data: Byte) {
        if (addr < 0 || addr >= memorySize) return
        mem[addr] = data
    }

    override fun writeWord(pos: Int, data: Int) {

        val a = (data and 0x000000FF).toByte()
        val b = (data and 0x0000FF00 shr 8).toByte()
        val c = (data and 0x00FF0000 shr 16).toByte()
        val d = (data and 0xFF000000.toInt() shr 24).toByte()

        if (littleEndian) {
            writeByte(pos, a)
            writeByte(pos + 1, b)
            writeByte(pos + 2, c)
            writeByte(pos + 3, d)
        } else {
            writeByte(pos, d)
            writeByte(pos + 1, c)
            writeByte(pos + 2, b)
            writeByte(pos + 3, a)
        }
    }

    override fun readWord(pos: Int): Int {

        val a = readByte(pos)
        val b = readByte(pos + 1)
        val c = readByte(pos + 2)
        val d = readByte(pos + 3)

        return if (littleEndian) {

            val ai = a.toInt() and 0xFF
            val bi = b.toInt() and 0xFF shl 8
            val ci = c.toInt() and 0xFF shl 16
            val di = d.toInt() and 0xFF shl 24

            ai or bi or ci or di
        } else {

            val di = d.toInt() and 0xFF
            val ci = c.toInt() and 0xFF shl 8
            val bi = b.toInt() and 0xFF shl 16
            val ai = a.toInt() and 0xFF shl 24

            ai or bi or ci or di
        }
    }

    override fun isLittleEndian(): Boolean = littleEndian

    override fun getMemorySize(): Int = size

    override fun serialize(): Map<String, Any> {
        return mapOf("mem" to mem.copyOf())
    }

    override fun deserialize(map: Map<String, Any>) {
        val data = map["mem"] as ByteArray
        System.arraycopy(data, 0, mem, 0, size)
    }
}