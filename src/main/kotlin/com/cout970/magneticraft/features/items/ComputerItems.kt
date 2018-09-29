package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.registry.ITEM_FLOPPY_DISK
import com.cout970.magneticraft.registry.fromItem
import com.cout970.magneticraft.systems.computer.FloppyDisk
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider


/**
 * Created by cout970 on 2017/08/10.
 */

object ComputerItems : IItemMaker {

    lateinit var floppyDisk: ItemBase private set

    val defaultDisks = mapOf(
        "user" to 0,
        "lisp" to 1,
        "forth" to 2,
        "shell" to 3,
        "basic" to 4,
        "editor" to 5,
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
}