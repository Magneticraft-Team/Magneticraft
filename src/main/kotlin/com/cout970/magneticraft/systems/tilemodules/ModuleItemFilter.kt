package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.internal.ApiUtils
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.forEach
import com.cout970.magneticraft.misc.inventory.isEmpty
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound

class ModuleItemFilter(
    val inventory: Inventory,
    override val name: String = "item_filter"
): IModule {
    override lateinit var container: IModuleContainer

    fun filterAllowStack(stack: ItemStack): Boolean {
        if (stack.isEmpty) return false
        if (inventory.isEmpty()) return true

        inventory.forEach {
            if (ApiUtils.equalsIgnoreSize(it, stack)) {
                return true
            }
        }
        return false
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("filter"))
    }

    override fun serializeNBT(): NBTTagCompound {
        return newNbt {
            add("filter", inventory.serializeNBT())
        }
    }
}