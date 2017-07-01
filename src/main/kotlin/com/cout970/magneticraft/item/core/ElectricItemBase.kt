package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.api.energy.item.IEnergyConsumerItem
import com.cout970.magneticraft.api.energy.item.IEnergyProviderItem
import com.cout970.magneticraft.api.energy.item.IEnergyStorageItem
import com.cout970.magneticraft.registry.ITEM_ENERGY_CONSUMER
import com.cout970.magneticraft.registry.ITEM_ENERGY_PROVIDER
import com.cout970.magneticraft.registry.ITEM_ENERGY_STORAGE
import com.cout970.magneticraft.util.getDouble
import com.cout970.magneticraft.util.setDouble
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.NonNullList
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/07/02.
 */
class ElectricItemBase : ItemBase() {

    companion object {
        val ENERGY_KEY = "energy"
    }

    var capacity = 0.0

    override fun getDurabilityForDisplay(stack: ItemStack): Double {
        if (getCapacityInternal(stack) < 1.0) return 1.0
        return 1 - (getStoredEnergyInternal(stack) / getCapacityInternal(stack))
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return getStoredEnergyInternal(stack) > 0
    }

    override fun getSubItems(itemIn: CreativeTabs, tab: NonNullList<ItemStack>) {
        if (itemIn == this.creativeTab) {
            tab.add(ItemStack(this, 1, 0))
            tab.add(ItemStack(this, 1, 0).apply {
                setDouble(ENERGY_KEY, getCapacityInternal(this))
            })
        }
    }

    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        tooltip.add(TextComponentTranslation("tooltip.magneticraft.item_storage.energy",
                getStoredEnergyInternal(stack),
                getCapacityInternal(stack)
        ).unformattedComponentText)
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    fun getStoredEnergyInternal(stack: ItemStack): Double = stack.getDouble(ENERGY_KEY)

    fun getCapacityInternal(stack: ItemStack): Double = capacity

    fun giveEnergyInternal(stack: ItemStack, power: Double, simulated: Boolean): Double {
        if (power <= 0.0) return 0.0
        val stored = getStoredEnergyInternal(stack)
        val space = getCapacityInternal(stack) - stored
        val min = Math.min(power, space)
        if (min > 0) {
            if (!simulated)
                stack.setDouble(ENERGY_KEY, stored + min)
            return min
        } else {
            return 0.0
        }
    }

    fun takeEnergyInternal(stack: ItemStack, power: Double, simulated: Boolean): Double {
        if (power <= 0.0) return 0.0
        val stored = getStoredEnergyInternal(stack)
        val min = Math.min(power, stored)
        if (min > 0) {
            if (!simulated)
                stack.setDouble(ENERGY_KEY, Math.max(0.0, stored - min))
            return min
        } else {
            return 0.0
        }
    }

    class ItemEnergyCapabilityProvider(val stack: ItemStack) : ICapabilityProvider {

        @Suppress("UNCHECKED_CAST")
        override fun <T> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            if (capability == ITEM_ENERGY_STORAGE) return DefaultItemEnergyStorage(stack) as T
            if (capability == ITEM_ENERGY_CONSUMER) return DefaultItemEnergyConsumer(stack) as T
            if (capability == ITEM_ENERGY_PROVIDER) return DefaultItemEnergyProvider(stack) as T
            return null
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
            return capability == ITEM_ENERGY_CONSUMER || capability == ITEM_ENERGY_STORAGE || capability == ITEM_ENERGY_PROVIDER
        }

        class DefaultItemEnergyConsumer(val stack: ItemStack) : IEnergyConsumerItem {

            override fun giveEnergy(power: Double, simulated: Boolean): Double {
                return (stack.item as ElectricItemBase).giveEnergyInternal(stack, power, simulated)
            }
        }

        class DefaultItemEnergyProvider(val stack: ItemStack) : IEnergyProviderItem {

            override fun takeEnergy(power: Double, simulated: Boolean): Double {
                return (stack.item as ElectricItemBase).takeEnergyInternal(stack, power, simulated)
            }
        }

        class DefaultItemEnergyStorage(val stack: ItemStack) : IEnergyStorageItem {

            override fun getStoredEnergy(): Double = (stack.item as ElectricItemBase).getStoredEnergyInternal(stack)

            override fun getCapacity(): Double = (stack.item as ElectricItemBase).getCapacityInternal(stack)
        }
    }
}

