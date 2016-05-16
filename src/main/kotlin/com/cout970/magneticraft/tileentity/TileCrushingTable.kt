package com.cout970.magneticraft.tileentity

import ITEM_HANDLER
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.registries.machines.crushingtable.crush
import com.cout970.magneticraft.api.registries.machines.crushingtable.isCrushable
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

val CRUSHING_DAMAGE = 40

class TileCrushingTable : TileBase() {
    var damageTaken = 0
    private val _inventory = CrushingTableInventory()
    val inventory: ItemStackHandler = _inventory

    fun hasStack() = inventory[0] != null

    fun getStack() = inventory[0]

    fun setStack(stack: ItemStack?) {
        inventory[0] = stack
    }

    fun canDamage() = hasStack() && getStack()!!.isCrushable()

    fun doDamage(amount: Int) {
        if (!canDamage()) {
            return
        }

        damageTaken += amount

        if (damageTaken >= CRUSHING_DAMAGE) {
            _inventory.setResult(getStack()?.crush()!!)
        }

        Minecraft.getMinecraft().thePlayer.sendChatMessage("$damageTaken ${inventory[0]}")
    }

    override fun writeToNBT(compound: NBTTagCompound) {
        super.writeToNBT(compound)

        compound.setTag("stack", NBTTagCompound().apply { getStack()?.writeToNBT(this) })
        compound.setInteger("damage", damageTaken)
    }

    override fun readFromNBT(compound: NBTTagCompound) {
        super.readFromNBT(compound)

        inventory[0] = if (compound.hasKey("stack"))
            ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"))
        else
            null

        damageTaken = compound.getInteger("damage")
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?) =
        (capability == ITEM_HANDLER) || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any?> getCapability(capability: Capability<T>?, facing: EnumFacing?) =
        if (capability == ITEM_HANDLER)
            inventory as T
        else
            super.getCapability(capability, facing)

    private inner class CrushingTableInventory : ItemStackHandler(1) {
        override fun onContentsChanged(slot: Int) {
            damageTaken = 0
        }

        override fun setStackInSlot(slot: Int, stack: ItemStack?) {
            if (stack?.isCrushable() ?: true) {
                super.setStackInSlot(slot, stack)
            }
        }

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            if (stack?.isCrushable() ?: true) {
                super.insertItem(slot, stack, simulate)
            }

            return stack
        }

        fun setResult(stack: ItemStack) {
            super.setStackInSlot(0, stack)
        }
    }
}