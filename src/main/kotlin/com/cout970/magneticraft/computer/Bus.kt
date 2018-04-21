package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IBus
import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import gnu.trove.map.TIntObjectMap

/**
 * Created by cout970 on 2016/09/30.
 */
class Bus(var ram: IRW, val devices: TIntObjectMap<IDevice>) : IBus {

    private fun Int.isExternal(): Boolean {
        return (this ushr 24) == 0xFF
    }

    override fun readByte(addr: Int): Byte {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = devices[ext] ?: return 0
            return dev.readByte(addr and 0xFFFF)
        }
        return ram.readByte(addr)
    }

    override fun writeByte(addr: Int, data: Byte) {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = devices[ext] ?: return
            dev.writeByte(addr and 0xFFFF, data)
        } else {
            ram.writeByte(addr, data)
        }
    }

    override fun writeWord(addr: Int, data: Int) {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = devices[ext] ?: return

            // @formatter:off
            val a = (data shr 24 and 0xFF).toByte()
            val b = (data shr 16 and 0xFF).toByte()
            val c = (data shr 8  and 0xFF).toByte()
            val d = (data        and 0xFF).toByte()

            dev.writeByte(addr + 3 and 0xFFFF, a)
            dev.writeByte(addr + 2 and 0xFFFF, b)
            dev.writeByte(addr + 1 and 0xFFFF, c)
            dev.writeByte(addr     and 0xFFFF, d)
            // @formatter:on
        } else {
            ram.writeWord(addr, data)
        }
    }

    override fun readWord(addr: Int): Int {
        if (addr.isExternal()) {
            val ext = addr shr 16 and 0xFF
            val dev = devices[ext] ?: return 0

            // @formatter:off
            val a = dev.readByte(addr + 3 and 0xFFFF)
            val b = dev.readByte(addr + 2 and 0xFFFF)
            val c = dev.readByte(addr + 1 and 0xFFFF)
            val d = dev.readByte(addr     and 0xFFFF)
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