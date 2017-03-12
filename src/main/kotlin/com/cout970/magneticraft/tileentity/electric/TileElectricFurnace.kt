package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.internal.energy.ElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.inventory.get
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitElectricity
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.ITEM_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.newNbt
import com.teamwizardry.librarianlib.common.util.autoregister.TileRegister
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.FurnaceRecipes
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.ItemStackHandler

/**
 * Created by cout970 on 04/07/2016.
 */
@TileRegister("electric_furnace")
class TileElectricFurnace : TileBase() {

    var mainNode = ElectricNode({ world }, { pos }, capacity = 1.25)

    val traitElectricity = TraitElectricity(this, listOf(mainNode))

    override val traits: List<ITileTrait> = listOf(traitElectricity)
    val inventory = Inventory()
    val production = ValueAverage()
    var burningTime = 0f

    override fun update() {
        if (worldObj.isServer) {
            if (mainNode.voltage >= ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE && canSmelt()) {
                val applied = mainNode.applyPower(
                        -Config.electricFurnaceMaxConsumption * interpolate(mainNode.voltage, 60.0, 70.0), false)
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

    override fun save(): NBTTagCompound {
        val nbt = newNbt {
            add("inventory", inventory.serializeNBT())
            add("meltingTime", burningTime)
        }
        return super.save().also { it.merge(nbt) }
    }

    override fun load(nbt: NBTTagCompound) {
        inventory.deserializeNBT(nbt.getCompoundTag("inventory"))
        burningTime = nbt.getFloat("meltingTime")
        super.load(nbt)
    }

    companion object {
        val MAX_BURNING_TIME = 100f //100 ticks => 5 seconds
        val SPEED = 2 // 2 times faster than a vanilla furnace
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == ITEM_HANDLER) return inventory as T
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == ITEM_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    override fun onBreak() {
        super.onBreak()
        if (worldObj.isServer) {
            (0 until inventory.slots)
                    .mapNotNull { inventory[it] }
                    .forEach { dropItem(it, pos) }
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