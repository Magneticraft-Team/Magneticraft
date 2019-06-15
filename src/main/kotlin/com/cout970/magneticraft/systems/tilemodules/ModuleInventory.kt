package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.forEach
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.world.dropItem
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.IItemHandler

/**
 * Created by cout970 on 2017/06/12.
 */
open class ModuleInventory(
    val inventory: Inventory,
    override val name: String = "module_inventory",
    val capabilityFilter: (IItemHandler) -> IItemHandler? = ALLOW_ALL,
    val sideFilter: (EnumFacing) -> Boolean = { true }
) : IModule {

    companion object {
        val ALLOW_NONE: (IItemHandler) -> IItemHandler? = { null }
        val ALLOW_ALL: (IItemHandler) -> IItemHandler? = { it }
    }

    override lateinit var container: IModuleContainer

    override fun onBreak() {
        inventory.forEach { item ->
            container.world.dropItem(item, container.pos)
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (cap == ITEM_HANDLER && (facing == null || sideFilter(facing))) {
            return capabilityFilter(inventory) as? T
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