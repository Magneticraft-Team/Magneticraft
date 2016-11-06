package com.cout970.magneticraft.tileentity.electric

import coffee.cypher.mcextlib.extensions.inventories.get
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.util.TIER_1_MACHINES_MIN_VOLTAGE
import com.cout970.magneticraft.util.misc.ValueAverage
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */
class TileElectricFurnace : TileElectricBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)
    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)
    val inventory = Inventory()
    var burningTime = 0f
    val production = ValueAverage()

    override fun update() {
        if (!worldObj.isRemote) {
            if (mainNode.voltage >= TIER_1_MACHINES_MIN_VOLTAGE && canSmelt()) {
                val applied = mainNode.applyPower(-Config.electricFurnaceMaxConsumption * interpolate(mainNode.voltage, 60.0, 70.0), false)
                burningTime += SPEED * applied.toFloat() / Config.electricFurnaceMaxConsumption.toFloat()
                production += applied
                if (burningTime > MAX_BURNING_TIME) {
                    smelt()
                    burningTime -= MAX_BURNING_TIME
                }
            }
            production.tick()
        }
        super.update()
    }

    fun canSmelt(): Boolean {
        //has input
        if (inventory[0] == null) return false
        //has recipe
        val result = FurnaceRecipes.instance().getSmeltingResult(inventory[0]) ?: return false
        //is output slot empty
        if (inventory[1] == null) return true
        //or can accept the result
        inventory.ignoreFilter = true
        val ret = inventory.insertItem(1, result, true) == null
        inventory.ignoreFilter = false
        return ret
    }

    fun smelt() {
        inventory.ignoreFilter = true
        val item = inventory.extractItem(0, 1, false)
        val result = FurnaceRecipes.instance().getSmeltingResult(item)?.copy()
        inventory.insertItem(1, result, false)
        inventory.ignoreFilter = false
    }

    override fun save(): NBTTagCompound = NBTTagCompound().apply {
        setTag("inventory", inventory.serializeNBT())
        setFloat("meltingTime", burningTime)
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        burningTime = nbt.getFloat("meltingTime")
    }

    companion object {
        val MAX_BURNING_TIME = 100f //100 ticks => 5 seconds
        val SPEED = 2 // 2 times faster than a vanilla furnace
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(capability: Capability<T>?, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>?, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    override fun onBreak() {
        super.onBreak()
        if (!worldObj.isRemote) {
            for (i in 0 until inventory.slots) {
                val item = inventory[i]
                if (item != null) {
                    dropItem(item, pos)
                }
            }
        }
    }

    inner class Inventory : ItemStackHandler(2) {

        var ignoreFilter = false

        override fun insertItem(slot: Int, stack: ItemStack?, simulate: Boolean): ItemStack? {
            if (slot == 0 || ignoreFilter) {
                return super.insertItem(slot, stack, simulate)
            }
            return stack
        }

        override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
            if (slot == 1 || ignoreFilter) {
                return super.extractItem(slot, amount, simulate)
            }
            return null
        }
    }
}