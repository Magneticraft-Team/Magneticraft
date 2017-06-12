package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.inventory.forEach
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 2017/06/12.
 */
open class ModuleInventory(
        size: Int,
        override val name: String = "module_inventory",
        val capabilityFilter: (IItemHandler) -> IItemHandler = { it },
        val onContentChange: (IItemHandler, Int) -> Unit = { inv, index -> Unit }
) : IModule {

    lateinit override var container: IModuleContainer

    val inventory = object : ItemStackHandler(size) {
        override fun onContentsChanged(slot: Int) = onContentChange(this, slot)
    }

    override fun onBreak() {
        inventory.forEach { item ->
            container.world.dropItem(item, container.pos)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (cap == ITEM_HANDLER) {
            return capabilityFilter(inventory) as T
        }
        return null
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag(name))
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            add(name, inventory.serializeNBT())
        }
    }
}