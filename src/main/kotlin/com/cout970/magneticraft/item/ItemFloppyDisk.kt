package com.cout970.magneticraft.item

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.util.getBoolean
import com.cout970.magneticraft.util.getInteger
import com.cout970.magneticraft.util.getString
import com.cout970.magneticraft.util.setString
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fml.common.FMLCommonHandler
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * Created by cout970 on 2016/10/14.
 */
object ItemFloppyDisk : ItemMod("floppy_disk", "normal", "bash") {


    init {
        maxStackSize = 1
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        val name = Random().ints(8).toArray().map { "0123456789ABCDEF"[it and 0xF] }.joinToString("")
        val name2 = Random().ints(8).toArray().map { "0123456789ABCDEF"[it and 0xF] }.joinToString("")

        subItems.add(ItemStack(itemIn, 1, 0).apply { tagCompound = createNBT(name, 128, true, true) })
        subItems.add(ItemStack(itemIn, 1, 1).apply { tagCompound = createNBT(name2, 128, true, false) })
    }

    override fun addInformation(stack: ItemStack?, playerIn: EntityPlayer?, tooltip: MutableList<String>?, advanced: Boolean) {
        super.addInformation(stack, playerIn, tooltip, advanced)
        tooltip?.add(ITEM_FLOPPY_DISK!!.fromItem(stack!!)?.label ?: "Unnamed")
    }

    fun createNBT(label: String, sectors: Int, read: Boolean, write: Boolean): NBTTagCompound {
        return NBTTagCompound().apply {
            setString("label", label)
            setInteger("sectorCount", sectors)
            setInteger("accessTime", 1)
            setBoolean("canRead", read)
            setBoolean("canWrite", write)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider {
        return object : ICapabilityProvider {

            override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
                if (capability == ITEM_FLOPPY_DISK) return FloppyDisk(stack) as T
                return null
            }

            override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
                return capability == ITEM_FLOPPY_DISK
            }
        }
    }

    private val cache = mutableMapOf<String, String>()

    class FloppyDisk(val stack: ItemStack) : IFloppyDisk {

        override fun getStorageFile(): File {
            if (stack.itemDamage == 0) {
                val parent = File(FMLCommonHandler.instance().savesDirectory, "./disks")
                if (!parent.exists()) parent.mkdir()
                val file = File(parent, "floppy_${label.replace(Pattern.compile("[^(\\w|\\d)]+").toRegex(), "_")}.img")
                return file
            } else {
                val parent = File(FMLCommonHandler.instance().savesDirectory, "./disks")
                if (!parent.exists()) parent.mkdir()
                val file: File
                if (stack.getString("label") in cache) {
                    file = File(cache[stack.getString("label")])
                    val bytes = ItemFloppyDisk::class.java.getResourceAsStream("/assets/${MOD_ID}/cpu/bash.bin").readBytes()
                    file.writeBytes(bytes)
                } else {
                    file = createTempFile(directory = parent)
                    val bytes = ItemFloppyDisk::class.java.getResourceAsStream("/assets/${MOD_ID}/cpu/bash.bin").readBytes()
                    file.writeBytes(bytes)
                    file.deleteOnExit()
                    cache.put(stack.getString("label"), file.absolutePath)
                }
                return file
            }
        }

        override fun getLabel(): String = stack.getString("label")

        override fun setLabel(str: String) = stack.setString("label", label)

        override fun getSectorCount(): Int = stack.getInteger("sectorCount")

        override fun getAccessTime(): Int = stack.getInteger("accessTime")

        override fun canRead(): Boolean = if (stack.itemDamage == 0) stack.getBoolean("canRead") else true

        override fun canWrite(): Boolean = if (stack.itemDamage == 0) stack.getBoolean("canWrite") else false
    }
}