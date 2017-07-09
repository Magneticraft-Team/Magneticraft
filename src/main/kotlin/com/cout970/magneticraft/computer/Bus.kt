package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IBus
import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW

/**
 * Created by cout970 on 2016/09/30.
 */
class Bus(var ram: IRW, val devices: Map<Int, IDevice>) : IBus {

    override fun readByte(addr: Int): Byte {
        if ((addr and 0xFF000000.toInt()) == 0xFF000000.toInt()) {
            val ext = addr and 0x00FF0000 shr 16
            val dev = devices[ext] ?: return 0
            return dev.readByte(addr and 0xFFFF)
        }
        return ram.readByte(addr)
    }

    override fun writeByte(addr: Int, data: Byte) {
        if ((addr and 0xFF000000.toInt()) == 0xFF000000.toInt()) {
            val ext = addr and 0x00FF0000 shr 16
            val dev = devices[ext] ?: return
            dev.writeByte(addr and 0xFFFF, data)
        } else {
            ram.writeByte(addr, data)
        }
    }

    override fun writeWord(addr: Int, data: Int) {
        if ((addr and 0xFF000000.toInt()) == 0xFF000000.toInt()) {
            val ext = addr and 0x00FF0000 shr 16
            val dev = devices[ext] ?: return
            dev.writeByte(addr + 3 and 0xFFFF, (data and 0xFF000000.toInt() shr 24).toByte())
            dev.writeByte(addr + 2 and 0xFFFF, (data and 0x00FF0000 shr 16).toByte())
            dev.writeByte(addr + 1 and 0xFFFF, (data and 0x0000FF00 shr 8).toByte())
            dev.writeByte(addr and 0xFFFF, (data and 0x000000FF).toByte())
        } else {
            ram.writeWord(addr, data)
        }
    }

    override fun readWord(addr: Int): Int {
        if ((addr and 0xFF000000.toInt()) == 0xFF000000.toInt()) {
            val ext = addr and 0x00FF0000 shr 16
            val dev = devices[ext] ?: return 0
            var data = 0
            data = data or (dev.readByte(addr + 3 and 0xFFFF).toInt() and 0xFF shl 24)
            data = data or (dev.readByte(addr + 2 and 0xFFFF).toInt() and 0xFF shl 16)
            data = data or (dev.readByte(addr + 1 and 0xFFFF).toInt() and 0xFF shl 8)
            data = data or (dev.readByte(addr and 0xFFFF).toInt() and 0xFF)
            return data
        }
        return ram.readWord(addr)
    }
}