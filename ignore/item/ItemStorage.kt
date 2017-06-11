package com.cout970.magneticraft.item

import com.cout970.magneticraft.api.energy.item.IEnergyConsumerItem
import com.cout970.magneticraft.api.energy.item.IEnergyProviderItem
import com.cout970.magneticraft.api.energy.item.IEnergyStorageItem
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.util.getDouble
import com.cout970.magneticraft.util.setDouble
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.text.TextComponentTranslation

/**
 * Created by cout970 on 2016/09/20.
 */
abstract class ItemStorage(registryName: String,
                           unlocalizedName: String = registryName)
: ItemMod(registryName, unlocalizedName) {

    companion object {
        val ENERGY_KEY = "energy"
    }

    init {
        maxStackSize = 1
    }

    override fun getDurabilityForDisplay(stack: ItemStack?): Double {
        if (getCapacityInternal(stack) < 1.0) return 1.0
        return 1 - (getStoredEnergyInternal(stack) / getCapacityInternal(stack))
    }

    override fun showDurabilityBar(stack: ItemStack): Boolean {
        return getStoredEnergyInternal(stack) > 0
    }

    override fun getSubItems(itemIn: Item, tab: CreativeTabs?, subItems: MutableList<ItemStack>) {
        subItems.add(ItemStack(itemIn, 1, 0))
        subItems.add(ItemStack(itemIn, 1, 0).apply { setDouble(ENERGY_KEY, Config.itemBatteryCapacity) })
    }

    override fun addInformation(stack: ItemStack?, playerIn: EntityPlayer?, tooltip: MutableList<String>?, advanced: Boolean) {
        tooltip?.add(TextComponentTranslation("tooltip.magneticraft.item_storage.energy", getStoredEnergyInternal(stack), Config.itemBatteryCapacity).unformattedComponentText)
        super.addInformation(stack, playerIn, tooltip, advanced)
    }

    fun getStoredEnergyInternal(stack: ItemStack?): Double = stack?.getDouble(ENERGY_KEY) ?: 0.0

    abstract fun getCapacityInternal(stack: ItemStack?): Double

    fun giveEnergyInternal(stack: ItemStack?, power: Double, simulated: Boolean): Double {
        if (power <= 0.0) return 0.0
        val stored = getStoredEnergyInternal(stack)
        val space = getCapacityInternal(stack) - stored
        val min = Math.min(power, space)
        if (min > 0) {
            if (!simulated)
                stack!!.setDouble(ENERGY_KEY, stored + min)
            return min
        } else {
            return 0.0
        }
    }

    fun takeEnergyInternal(stack: ItemStack?, power: Double, simulated: Boolean): Double {
        if (power <= 0.0) return 0.0
        val stored = getStoredEnergyInternal(stack)
        val min = Math.min(power, stored)
        if (min > 0) {
            if (!simulated)
                stack!!.setDouble(ENERGY_KEY, Math.max(0.0, stored - min))
            return min
        } else {
            return 0.0
        }
    }

    class DefaultItemEnergyConsumer(val stack: ItemStack, val item: ItemStorage) : IEnergyConsumerItem {

        override fun giveEnergy(power: Double, simulated: Boolean): Double {
            return item.giveEnergyInternal(stack, power, simulated)
        }
    }

    class DefaultItemEnergyProvider(val stack: ItemStack, val item: ItemStorage) : IEnergyProviderItem {

        override fun takeEnergy(power: Double, simulated: Boolean): Double {
            return item.takeEnergyInternal(stack, power, simulated)
        }
    }

    class DefaultItemEnergyStorage(val stack: ItemStack, val item: ItemStorage) : IEnergyStorageItem {

        override fun getStoredEnergy(): Double = item.getStoredEnergyInternal(stack)

        override fun getCapacity(): Double = item.getCapacityInternal(stack)
    }
}