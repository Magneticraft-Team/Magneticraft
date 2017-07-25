package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import net.minecraft.nbt.NBTTagCompound
import java.io.RandomAccessFile
import java.nio.charset.Charset
import java.util.*

/**
 * Created by cout970 on 2016/10/13.
 */
class DeviceFloppyDrive(val parent: ITileRef, val getDisk: () -> IFloppyDisk?) : IDevice, ITileRef by parent {

    private var buffer: ByteArray? = null
    var sleep = 0
    var status = 0
    var action = 0
    var currentSector = 0

    val isActive: Boolean = true

    override fun getId(): NodeID = NodeID("module_device_floppy_drive", pos, world)

    fun iterate() {
        if (sleep > 0) {
            sleep--
            return
        }
        status = 0
        if (action != 0) {
            when (action) {
                1 -> read()
                2 -> write()
                3 -> readLabel()
                4 -> writeLabel()
            }
            action = 0
        }
    }

    fun read() {
        val floppy = getDisk() ?: return
        if (currentSector >= 0 && currentSector < floppy.sectorCount && floppy.canRead()) {
            try {
                val file = floppy.storageFile
                if (file.exists()) {
                    val map = RandomAccessFile(file, "r")
                    map.seek(currentSector * 1024L)
                    val read = map.read(getBuffer())
                    if (read == -1) {
                        Arrays.fill(getBuffer(), 0)
                    } else if (read != 1024) {
                        getBuffer().fill(0, read, 1024)
                    }
//                    val checksum = getBuffer().sumBy { it.toInt() and 0xFF }
                    map.close()
                } else {
                    Arrays.fill(getBuffer(), 0)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sleep = floppy.accessTime
            status = 1
        }
    }

    fun write() {
        val floppy = getDisk() ?: return
        if (currentSector >= 0 && currentSector < floppy.sectorCount && floppy.canWrite()) {
            try {
                val file = floppy.storageFile
                val map = RandomAccessFile(file, "rw")
                map.seek(currentSector * 1024L)
                map.write(getBuffer())
                map.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sleep = floppy.accessTime
            status = 2
        }
    }

    fun readLabel() {
        val floppy = getDisk() ?: return
        if (currentSector >= 0 && currentSector < floppy.sectorCount) {
            try {
                Arrays.fill(getBuffer(), 0)
                val array = floppy.label.toByteArray(Charset.forName("US-ASCII"))
                System.arraycopy(array, 0, getBuffer(), 0, Math.min(array.size, 1024))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sleep = floppy.accessTime
            status = 1
        }
    }

    fun writeLabel() {
        val floppy = getDisk() ?: return
        if (currentSector >= 0 && currentSector < floppy.sectorCount) {
            try {
                floppy.label = getBuffer().toString(Charset.forName("US-ASCII"))
            } catch (e: Exception) {
                e.printStackTrace()
            }
            sleep = floppy.accessTime
            status = 2
        }
    }

    val memStruct = ReadWriteStruct("disk_drive_header",
            ReadOnlyByte("online", { if (isActive) 1 else 0 }),
            ReadOnlyByte("type", { 0 }),
            ReadOnlyShort("status", { status.toShort() }),

            ReadWriteByte("signal", { action = it.toInt() }, { action.toByte() }),
            ReadOnlyByte("hasDisk", { if (getDisk() == null) 0 else 1 }),
            ReadOnlyByte("accessTime", { getDisk()?.accessTime?.toByte() ?: 0 }),
            ReadOnlyByte("padding", { 0 }),

            ReadOnlyInt("numSectors", { getDisk()?.sectorCount ?: 0 }),
            ReadWriteInt("currentSector", { currentSector = it }, { currentSector }),
            ReadWriteByteArray("buffer", getBuffer())
    )

    init {
        println(memStruct)
    }

    override fun readByte(addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun writeByte(addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    fun getBuffer(): ByteArray {
        if (buffer == null || buffer!!.size != 1024) buffer = ByteArray(1024)
        return buffer!!
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        sleep = nbt.getInteger("sleep")
        status = nbt.getInteger("status")
        action = nbt.getInteger("action")
        currentSector = nbt.getInteger("sector")
        System.arraycopy(nbt.getByteArray("buffer"), 0, getBuffer(), 0, getBuffer().size)
    }

    override fun serializeNBT(): NBTTagCompound {
        return NBTTagCompound().apply {
            setInteger("sleep", sleep)
            setInteger("status", status)
            setInteger("action", action)
            setInteger("sector", currentSector)
            setByteArray("buffer", getBuffer())
        }
    }
}