package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.util.split
import com.cout970.magneticraft.util.splitRange
import com.cout970.magneticraft.util.splitSet
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos

/**
 * Created by cout970 on 2016/10/01.
 */
class DeviceMotherboard(val tile: ITileRef, val mb: Motherboard) : IDevice, ITileRef by tile {

    override fun getId(): NodeID = NodeID("module_device_motherboard", pos, world.provider.dimension)

    override fun deserializeNBT(nbt: NBTTagCompound) = Unit
    override fun serializeNBT(): NBTTagCompound = NBTTagCompound()

    var logInt = 0
    var logType = 0

    override fun readByte(addr: Int): Byte {
        when (addr) {
            0 -> return if (mb.isOnline()) 1 else 0 //online
            1 -> return 0 //on/off request signal

            2 -> return 1 //monitor id
            3 -> return 0 //floppy id

        //devices 0-16
            4, 5, 6, 7 -> return if (0 in mb.bus.devices) (0xFF000000).split(addr - 4) else 0
            8, 9, 10, 11 -> return if (1 in mb.bus.devices) (0xFF010000).split(addr - 8) else 0
            12, 13, 14, 15 -> return if (2 in mb.bus.devices) (0xFF020000).split(addr - 12) else 0
            16, 17, 18, 19 -> return if (3 in mb.bus.devices) (0xFF030000).split(addr - 16) else 0

            20, 21, 22, 23 -> return if (4 in mb.bus.devices) (0xFF040000).split(addr - 20) else 0
            24, 25, 26, 27 -> return if (5 in mb.bus.devices) (0xFF050000).split(addr - 24) else 0
            28, 29, 30, 31 -> return if (6 in mb.bus.devices) (0xFF060000).split(addr - 28) else 0
            32, 33, 34, 35 -> return if (7 in mb.bus.devices) (0xFF070000).split(addr - 32) else 0

            36, 37, 38, 39 -> return if (8 in mb.bus.devices) (0xFF080000).split(addr - 36) else 0
            40, 41, 42, 43 -> return if (9 in mb.bus.devices) (0xFF090000).split(addr - 40) else 0
            44, 45, 46, 47 -> return if (10 in mb.bus.devices) (0xFF0a0000).split(addr - 44) else 0
            48, 49, 50, 51 -> return if (11 in mb.bus.devices) (0xFF0b0000).split(addr - 48) else 0

            52, 53, 54, 55 -> return if (12 in mb.bus.devices) (0xFF0c0000).split(addr - 52) else 0
            56, 57, 58, 59 -> return if (13 in mb.bus.devices) (0xFF0d0000).split(addr - 56) else 0
            60, 61, 62, 63 -> return if (14 in mb.bus.devices) (0xFF0e0000).split(addr - 60) else 0
            64, 65, 66, 67 -> return if (15 in mb.bus.devices) (0xFF0f0000).split(addr - 64) else 0
        //memSize
            68, 69, 70, 71 -> return mb.memory.memorySize.split(addr - 68)
        //littleEndian
            72, 73, 74, 75 -> return (if (mb.memory.isLittleEndian) -1 else 0).split(addr - 72)
        //worldTime
            76, 77, 78, 79 -> return mb.clock.split(addr - 76)
        //run time
            80, 81, 82, 83 -> return getWorldTime().split(addr - 80)
        }
        return 0
    }

    fun getWorldTime(): Long {
        if (tile is FakeRef) return 0L
        return world.totalWorldTime
    }

    fun getComputerPos(): BlockPos {
        if (tile is FakeRef) return BlockPos.ORIGIN
        return pos
    }

    override fun writeByte(addr: Int, data: Byte) {
        when (addr) {
            1 -> {//on/off signal
                when (data.toInt()) {
                    0 -> mb.halt()
                    1 -> mb.start()
                    2 -> mb.reset()
                }
            }
            84 -> {
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
            85 -> {
                mb.sleep(data.toInt())
            }
            86 -> {
                logType = data.toInt()
            }
            in 88.splitRange() -> {
                logInt = logInt.splitSet(addr - 88, data)
                if (addr == 88) {
                    when (logType) {
                        1 -> print("$logInt ")
                        2 -> print("0x%08x ".format(logInt))
                        3 -> print(logInt.toChar())
                        else -> {
                            println("${getComputerPos()}: 0x%08x, %08d".format(logInt, logInt))
                        }
                    }
                }
            }
        }
    }
}