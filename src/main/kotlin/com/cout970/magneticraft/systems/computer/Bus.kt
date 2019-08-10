package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW

/**
 * Created by cout970 on 2016/09/30.
 */
class Bus(var ram: IRW, val getDevice: (Int) -> IDevice?) : IRW {

    private fun Int.isExternal(): Boolean {
        return (this ushr 24) == 0xFF
    }

    override fun readByte(addr: Int): Byte {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = getDevice(ext) ?: return 0
            return dev.readByte(this, addr and 0xFFFF)
        }
        return ram.readByte(addr)
    }

    override fun writeByte(addr: Int, data: Byte) {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = getDevice(ext) ?: return
            dev.writeByte(this, addr and 0xFFFF, data)
        } else {
            ram.writeByte(addr, data)
        }
    }

    override fun writeWord(addr: Int, data: Int) {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = getDevice(ext) ?: return

            // @formatter:off
            val a = (data shr 24 and 0xFF).toByte()
            val b = (data shr 16 and 0xFF).toByte()
            val c = (data shr 8  and 0xFF).toByte()
            val d = (data        and 0xFF).toByte()

            dev.writeByte(this, addr + 3 and 0xFFFF, a)
            dev.writeByte(this, addr + 2 and 0xFFFF, b)
            dev.writeByte(this, addr + 1 and 0xFFFF, c)
            dev.writeByte(this, addr     and 0xFFFF, d)
            // @formatter:on
        } else {
            ram.writeWord(addr, data)
        }
    }

    override fun readWord(addr: Int): Int {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = getDevice(ext) ?: return 0

            // @formatter:off
            val a = dev.readByte(this, addr + 3 and 0xFFFF)
            val b = dev.readByte(this, addr + 2 and 0xFFFF)
            val c = dev.readByte(this, addr + 1 and 0xFFFF)
            val d = dev.readByte(this, addr     and 0xFFFF)
            // @formatter:on

            val ai = a.toInt() and 0xFF shl 24
            val bi = b.toInt() and 0xFF shl 16
            val ci = c.toInt() and 0xFF shl 8
            val di = d.toInt() and 0xFF

            return ai or bi or ci or di
        }
        return ram.readWord(addr)
    }
}