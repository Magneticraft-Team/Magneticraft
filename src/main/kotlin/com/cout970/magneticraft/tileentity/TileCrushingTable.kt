package com.cout970.magneticraft.tileentity

import ITEM_HANDLER
import coffee.cypher.mcextlib.extensions.inventories.get
import coffee.cypher.mcextlib.extensions.inventories.set
import com.cout970.magneticraft.api.registries.machines.crushingtable.crush
import com.cout970.magneticraft.api.registries.machines.crushingtable.findCrushable
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

val CRUSHING_DAMAGE = 40

class TileCrushingTable : TileBase() {
    var damageTaken = 0
    private val inventory = CrushingTableInventory()

    fun canDamage() = inventory[0] != null

    fun doDamage(amount: Int) {
        if (!canDamage()) {
            return
        }

        damageTaken += amount

        if (damageTaken >= CRUSHING_DAMAGE) {
            inventory[0] = inventory[0]?.crush()
        }
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

        override fun getStackLimit(slot: Int, stack: ItemStack?) =
            stack?.findCrushable()?.stackSize ?: 0
    }
}