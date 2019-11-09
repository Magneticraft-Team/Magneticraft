package com.cout970.magneticraft.systems.items

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.misc.getInteger
import com.cout970.magneticraft.misc.setInteger
import com.cout970.magneticraft.misc.tc
import com.cout970.magneticraft.registry.FORGE_ENERGY
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemStack
import net.minecraft.util.text.ITextComponent
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.energy.IEnergyStorage

/**
 * Created by cout970 on 2017/07/02.
 */
class ElectricItemBase(properties: Properties, var capacity: Int = 0) : ItemBase(properties) {

    companion object {
        const val ENERGY_KEY = "energy"
    }

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        if (getCapacityInternal(stack) < 1.0) return 1.0
        return 1 - (getStoredEnergyInternal(stack).toDouble() / getCapacityInternal(stack))
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return getStoredEnergyInternal(stack) > 0
    }


    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<ITextComponent>, flagIn: ITooltipFlag) {
        tooltip.add(tc("tooltip.magneticraft.item_storage.energy",
            getStoredEnergyInternal(stack),
            getCapacityInternal(stack)
        ))
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    fun getStoredEnergyInternal(stack: ItemStack): Int = stack.getInteger(ENERGY_KEY)

    @Suppress("UNUSED_PARAMETER")
    fun getCapacityInternal(stack: ItemStack): Int = capacity

    fun giveEnergyInternal(stack: ItemStack, power: Int, simulated: Boolean): Int {
        if (power <= 0.0) return 0
        val stored = getStoredEnergyInternal(stack)
        val space = getCapacityInternal(stack) - stored
        val min = Math.min(power, space)
        if (min > 0) {
            if (!simulated)
                stack.setInteger(ENERGY_KEY, stored + min)
            return min
        } else {
            return 0
        }
    }

    fun takeEnergyInternal(stack: ItemStack, power: Int, simulated: Boolean): Int {
        if (power <= 0.0) return 0
        val stored = getStoredEnergyInternal(stack)
        val min = Math.min(power, stored)
        if (min > 0) {
            if (!simulated)
                stack.setInteger(ENERGY_KEY, Math.max(0, stored - min))
            return min
        } else {
            return 0
        }
    }

    class ItemEnergyCapabilityProvider(val stack: ItemStack) : ICapabilityProvider {

        @Suppress("UNCHECKED_CAST")
        override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): LazyOptional<T> {
            if (capability == FORGE_ENERGY) return LazyOptional.of { DefaultItemEnergyStorage(stack) }.cast()
            return LazyOptional.empty()
        }

        class DefaultItemEnergyStorage(val stack: ItemStack) : IEnergyStorage {

            override fun canReceive(): Boolean = true
            override fun canExtract(): Boolean = true

            override fun getMaxEnergyStored(): Int = (stack.item as ElectricItemBase).getCapacityInternal(stack)

            override fun getEnergyStored(): Int = (stack.item as ElectricItemBase).getStoredEnergyInternal(stack)

            override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int {
                return (stack.item as ElectricItemBase).takeEnergyInternal(stack, maxExtract, simulate)
            }

            override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
                return (stack.item as ElectricItemBase).giveEnergyInternal(stack, maxReceive, simulate)
            }
        }
    }
}

