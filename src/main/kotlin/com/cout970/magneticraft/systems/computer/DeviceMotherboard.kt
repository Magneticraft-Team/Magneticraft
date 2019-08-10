package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.split
import com.cout970.magneticraft.misc.splitSet
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2016/10/01.
 */
class DeviceMotherboard(val tile: ITileRef, val mb: Motherboard) : IDevice, ITileRef by tile {

    var logType = 0

    val memStruct = ReadWriteStruct("motherboard_header",
        ReadOnlyByte("online") { if (mb.isOnline) 1 else 0 },
        ReadWriteByte("signal", { signal(it.toInt()) }, { 0 }),
        ReadWriteByte("sleep", { mb.sleep(it.toInt()) }, { 0 }),
        ReadOnlyByte("padding") { 0 },
        ReadOnlyInt("memSize") { mb.ram.memorySize },
        ReadOnlyInt("littleEndian") { if (mb.ram.isLittleEndian) -1 else 0 },
        ReadOnlyInt("worldTime") { (getWorldTime() and 0xFFFFFFFF).toInt() },
        ReadOnlyInt("cpuTime") { mb.clock },
        ReadWriteByte("logType", { logType = it.toInt() }, { logType.toByte() }),
        ReadWriteByte("logByte", { logByte(it) }, { 0 }),
        LogShort("logShort") { logShort(it) },
        LogInt("logInt") { logInt(it) },
        ReadOnlyIntArray("devices") { getDevices() },
        ReadOnlyInt("*monitor") { 0xFF000000.toInt() },
        ReadOnlyInt("*floppy") { 0xFF010000.toInt() },
        ReadOnlyInt("*keyboard") { 0xFF020000.toInt() }
    )

    override fun update() = Unit

    fun getDevices(): IntArray {
        val buffer = IntArray(16)
        repeat(16) {
            if (it in mb.deviceMap.keySet()) {
                buffer[it] = 0xFF000000.toInt() or (it shl 16)
            }
        }
        return buffer
    }

    fun signal(id: Int) {
        when (id) {
            0 -> mb.halt()
            1 -> mb.start()
            2 -> mb.reset()
        }
    }

    fun logByte(data: Byte) {
        when (logType) {
            1 -> print("${data.toInt() and 0xFF} ")
            2 -> print("0x%02x ".format(data.toInt() and 0xFF))
            3 -> print(data.toChar())
            else -> {
                println("${getComputerPos()}: 0x%02x, %03d, ".format(data.toInt() and 0xFF,
                    data.toInt() and 0xFF) + data.toChar())
            }
        }
    }

    fun logShort(data: Short) {
        when (logType) {
            1 -> print("$data ")
            2 -> print("0x%04x ".format(data))
            3 -> print(data.toChar())
            else -> {
                println("${getComputerPos()}: 0x%04x, %05d".format(data, data))
            }
        }
    }

    fun logInt(data: Int) {
        when (logType) {
            1 -> print("$data ")
            2 -> print("0x%08x ".format(data))
            3 -> print(data.toChar())
            else -> {
                println("${getComputerPos()}: 0x%08x, %010d".format(data, data))
            }
        }
    }

    fun getWorldTime(): Long {
        if (tile == FakeRef) return 0L
        return world.totalWorldTime
    }

    fun getComputerPos(): BlockPos {
        if (tile == FakeRef) return BlockPos.ORIGIN
        return pos
    }

    override fun readByte(bus: IRW, addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun writeByte(bus: IRW, addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    class LogInt(val name: String, val log: (Int) -> Unit) : IVariable {

        var logInt = 0
        override val size = 4

        override fun read(addr: Int) = logInt.split(addr)

        override fun write(addr: Int, value: Byte) {
            logInt = logInt.splitSet(addr, value)
            if (addr == 0) log(logInt)
        }

        override fun toString(): String = "i32 $name;"
    }

    class LogShort(val name: String, val log: (Short) -> Unit) : IVariable {

        var logShort = 0
        override val size = 2

        override fun read(addr: Int) = logShort.split(addr)

        override fun write(addr: Int, value: Byte) {
            logShort = logShort.splitSet(addr, value)
            if (addr == 0) log(logShort.toShort())
        }

        override fun toString(): String = "i16 $name;"
    }

    override fun serialize() = mapOf("logType" to logType)

    override fun deserialize(map: Map<String, Any>) {
        logType = map["logType"] as Int
    }
}
