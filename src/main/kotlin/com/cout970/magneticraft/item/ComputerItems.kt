package com.cout970.magneticraft.item

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.util.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.common.FMLCommonHandler
import java.io.File
import java.util.*


/**
 * Created by cout970 on 2017/08/10.
 */

object ComputerItems : IItemMaker {

    lateinit var floppyDisk: ItemBase private set

    val defaultDisks = mapOf(
            "user" to 0,
            "lisp" to 1,
            "forth" to 2,
            "drivers" to 3,
            "basic" to 4,
            "vim" to 5,
            "asm" to 6
    )

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
        }

        floppyDisk = builder.withName("floppy_disk").copy {
            variants = defaultDisks.map { it.value to it.key }.toMap()
            capabilityProvider = {
                if (it.stack.tagCompound == null) fillNBT(it.stack)
                FloppyDiskCapabilityProvider(it.stack)
            }
            addInformation = {
                it.tooltip.add(ITEM_FLOPPY_DISK!!.fromItem(it.stack)?.label ?: "Unnamed")
            }
            maxStackSize = 1
            createStack = { item, amount, meta -> ItemStack(item, amount, meta).also { fillNBT(it) } }
        }.build()

        return listOf(floppyDisk)
    }

    fun fillNBT(stack: ItemStack) {
        stack.tagCompound = createNBT(128, true, true)
    }

    fun createNBT(sectors: Int, read: Boolean, write: Boolean): NBTTagCompound {
        return newNbt {
            add("label", "Unnamed")
            add("sectorCount", sectors)
            add("accessTime", 1)
            add("canRead", read)
            add("canWrite", write)
        }
    }

    // Avoid loading the same floppy drive twice
    private val cache = mutableMapOf<String, String>()

    @Suppress("UNCHECKED_CAST")
    class FloppyDiskCapabilityProvider(val stack: ItemStack) : ICapabilityProvider {

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            if (capability == ITEM_FLOPPY_DISK) return FloppyDisk(stack) as T
            return null
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability == ITEM_FLOPPY_DISK
        }
    }

    class FloppyDisk(val stack: ItemStack) : IFloppyDisk {

        override fun getStorageFile(): File {

            if (stack.itemDamage == 0) { // user created disks
                val parent = File(FMLCommonHandler.instance().savesDirectory, "./disks")
                if (!parent.exists()) {
                    parent.mkdir()
                }
                return File(parent, "floppy_${serialNumber.toHex()}.img")

            } else {
                // disks copied from pre-existent ROMs
                val parent = File(FMLCommonHandler.instance().savesDirectory, "./disks")
                if (!parent.exists()) {
                    parent.mkdir()
                }
                val file: File
                val source = (defaultDisks.toList().find { it.second == stack.itemDamage }?.first ?: "bios") + ".bin"

                val bytes = ComputerItems::class.java
                                    .getResourceAsStream("/assets/$MOD_ID/cpu/$source")
                                    ?.readBytes() ?: ByteArray(0)

                if (stack.getString("label") in cache) {
                    file = File(cache[stack.getString("label")])
                    file.writeBytes(bytes)
                } else {
                    file = createTempFile(directory = parent)
                    file.writeBytes(bytes)
                    file.deleteOnExit()
                    cache[stack.getString("label")] = file.absolutePath
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
                        .let { Integer.parseInt(it, 16) }

                nbt.add("serialNumber", num)
            }
            return nbt.getInteger("serialNumber")
        }

        override fun getLabel(): String {
            return stack.getString("label")
        }

        override fun setLabel(str: String) = stack.setString("label", label)

        override fun getSectorCount(): Int = stack.getInteger("sectorCount")

        override fun getAccessTime(): Int = stack.getInteger("accessTime")

        override fun canRead(): Boolean = if (stack.itemDamage == 0) stack.getBoolean("canRead") else true

        override fun canWrite(): Boolean = if (stack.itemDamage == 0) stack.getBoolean("canWrite") else false
    }
}