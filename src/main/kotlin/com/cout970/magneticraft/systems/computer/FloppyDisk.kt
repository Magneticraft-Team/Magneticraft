package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.features.items.ComputerItems
import com.cout970.magneticraft.hasKey
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.world.worldSaveDirectory
import net.minecraft.item.ItemStack
import java.io.File
import java.util.*

class FloppyDisk(val stack: ItemStack) : IFloppyDisk {

    companion object {
        // Avoid loading the same floppy drive twice
        private val cache = mutableMapOf<Int, String>()
    }

    @SuppressWarnings("deprecation")
    override fun getStorageFile(): File {

        if (stack.damage == 0) {
            // user created disks
            val parent = File(worldSaveDirectory(), "./disks")

            if (!parent.exists()) parent.mkdir()

            return File(parent, "floppy_${serialNumber.toHex()}.img")

        } else {
            // disks copied from pre-existent ROMs
            val parent = File(worldSaveDirectory(), "./disks")
            if (!parent.exists()) parent.mkdir()

            val baseName = when (stack.item) {
                ComputerItems.floppyDiskLisp -> "lisp"
                ComputerItems.floppyDiskForth -> "forth"
                ComputerItems.floppyDiskShell -> "shell"
                ComputerItems.floppyDiskBasic -> "basic"
                ComputerItems.floppyDiskEditor -> "editor"
                ComputerItems.floppyDiskAsm -> "asm"
                else -> "bios"
            }

            val source = "$baseName.bin"

            val file: File
            val serial = serialNumber

            if (serial in cache) {
                file = File(cache[serial]!!)

                if (Debug.DEBUG) {
                    val bytes = ComputerItems::class.java
                        .getResourceAsStream("/assets/$MOD_ID/cpu/$source")
                        ?.readBytes()
                        ?: ByteArray(0)

                    file.writeBytes(bytes)
                }

            } else {
                val bytes = ComputerItems::class.java
                    .getResourceAsStream("/assets/$MOD_ID/cpu/$source")
                    ?.readBytes()
                    ?: ByteArray(0)

                file = createTempFile(directory = parent)
                file.writeBytes(bytes)
                file.deleteOnExit()
                cache[serial] = file.absolutePath
            }
            return file
        }
    }

    override fun getSerialNumber(): Int {
        val nbt = stack.checkNBT()
        if (!nbt.hasKey("serialNumber")) {

            val num = Random()
                .ints(8)
                .toArray()
                .map { "0123456789ABCDEF"[it and 0xF] }
                .joinToString("")
                .let { java.lang.Long.decode("0x$it").toInt() }

            nbt.add("serialNumber", num)
        }
        return nbt.getInt("serialNumber")
    }

    override fun getLabel(): String {
        return stack.getString("label")
    }

    override fun setLabel(str: String) = stack.setString("label", str)

    override fun getSectorCount(): Int = stack.getInteger("sectorCount")

    override fun getAccessTime(): Int = stack.getInteger("accessTime")

    override fun canRead(): Boolean = if (stack.damage == 0) stack.getBoolean("canRead") else true

    override fun canWrite(): Boolean = if (stack.damage == 0) stack.getBoolean("canWrite") else false
}