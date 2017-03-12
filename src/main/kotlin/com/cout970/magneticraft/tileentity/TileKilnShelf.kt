package com.cout970.magneticraft.tileentity

import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

@TileRegister("kiln_shelf")
class TileKilnShelf : TileBase(), ITickable {

    val inventory: KilnShelfInventory = KilnShelfInventory()

    override fun update() {
        if (worldObj.isServer && shouldTick(100)) {
            sendUpdateToNearPlayers()
        }
    }

    fun getStack() = inventory[0]?.copy()

    fun setStack(stack: ItemStack?) {
        inventory[0] = stack?.copy()
    }

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            if (getStack() != null) {
                add("stack", getStack()!!.serializeNBT())
            }
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory[0] = if (nbt.hasKey("stack")) {
            ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("stack"))
        } else {
            null
        }
        super.load(nbt)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?) =
            (capability == ITEM_HANDLER) || super.hasCapability(capability, facing)

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        return if (capability == ITEM_HANDLER) {
            inventory as T
        } else {
            super.getCapability(capability, facing)
        }
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
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