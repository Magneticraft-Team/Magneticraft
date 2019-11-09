package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.NBTTagCompound
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.computer.FloppyDisk
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import com.cout970.magneticraft.tagCompound
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.Direction
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional


/**
 * Created by cout970 on 2017/08/10.
 */
@RegisterItems
object ComputerItems : IItemMaker {

    lateinit var floppyDisk: ItemBase private set
    lateinit var floppyDiskLisp: ItemBase private set
    lateinit var floppyDiskForth: ItemBase private set
    lateinit var floppyDiskShell: ItemBase private set
    lateinit var floppyDiskBasic: ItemBase private set
    lateinit var floppyDiskEditor: ItemBase private set
    lateinit var floppyDiskAsm: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            capabilityProvider = {
                if (it.stack.tagCompound == null) fillNBT(it.stack)
                FloppyDiskCapabilityProvider(it.stack)
            }
            addInformation = {
                val msg = ITEM_FLOPPY_DISK!!.fromItem(it.stack)?.label ?: "Unnamed"
                it.tooltip.add(msg.toTextComponent())
            }
            maxStackSize = 1
            postCreate = { fillNBT(it) }
        }

        floppyDisk = builder.withName("floppy_disk").build()
        floppyDiskLisp = builder.withName("floppy_disk_lisp").build()
        floppyDiskForth = builder.withName("floppy_disk_forth").build()
        floppyDiskShell = builder.withName("floppy_disk_shell").build()
        floppyDiskBasic = builder.withName("floppy_disk_basic").build()
        floppyDiskEditor = builder.withName("floppy_disk_editor").build()
        floppyDiskAsm = builder.withName("floppy_disk_asm").build()

        return listOf(
            floppyDisk, floppyDiskLisp, floppyDiskForth, floppyDiskShell, floppyDiskBasic,floppyDiskEditor,
            floppyDiskAsm
        )
    }

    @Suppress("UNCHECKED_CAST")
    class FloppyDiskCapabilityProvider(val stack: ItemStack) : ICapabilityProvider {
        override fun <T : Any?> getCapability(cap: Capability<T>, side: Direction?): LazyOptional<T> {
            if (cap == ITEM_FLOPPY_DISK) return LazyOptional.of { FloppyDisk(stack) }.cast()
            return LazyOptional.empty()
        }
    }

    fun fillNBT(stack: ItemStack) {
        stack.tagCompound = createNBT(128, read = true, write = true)
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
}