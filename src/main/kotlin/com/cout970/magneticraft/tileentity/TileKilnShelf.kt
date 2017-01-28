package com.cout970.magneticraft.tileentity

import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.util.shouldTick
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

class TileKilnShelf : TileBase(), ITickable {

    val inventory: KilnShelfInventory = KilnShelfInventory()

    override fun update() {
        if (!worldObj.isRemote && shouldTick(100)) {
            sendUpdateToNearPlayers()
        }
    }

    fun getStack() = inventory[0]?.copy()

    fun setStack(stack: ItemStack?) {
        inventory[0] = stack?.copy()
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        if (getStack() != null) {
            setTag("stack", NBTTagCompound().apply { getStack()?.writeToNBT(this) })
        }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory[0] = if (nbt.hasKey("stack")) {
            ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"))
        } else {
            null
        }
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?) =
            (capability == ITEM_HANDLER) || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        return if (capability == ITEM_HANDLER) {
            inventory as T
        } else {
            super.getCapability(capability, facing)
        }
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            if (inventory[0] != null) {
                dropItem(inventory[0]!!, pos)
            }
        }
    }

    inner class KilnShelfInventory : ItemStackHandler(1) {

        override fun getStackLimit(slot: Int, stack: ItemStack): Int = 1

        fun setResult(stack: ItemStack) {
            super.setStackInSlot(0, stack)
        }
    }
}