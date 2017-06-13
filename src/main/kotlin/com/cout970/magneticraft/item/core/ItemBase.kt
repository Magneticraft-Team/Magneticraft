package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.MOD_ID
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.NonNullList
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/11.
 */
open class ItemBase : Item() {

    var onHitEntity: ((HitEntityArgs) -> Boolean)? = null
    var capabilityProvider: ((InitCapabilitiesArgs) -> ICapabilityProvider?)? = null
    var variants: Map<Int, String> = mapOf(0 to "normal")

    override fun getUnlocalizedName(): String = "item.$MOD_ID.${registryName?.resourcePath}"

    override fun getUnlocalizedName(stack: ItemStack): String = "${unlocalizedName}_${variants[stack.metadata]}"

    override fun getHasSubtypes() = variants.size > 1

    override fun getSubItems(itemIn: CreativeTabs, tab: NonNullList<ItemStack>) {
        if (itemIn == this.creativeTab) {
            variants.keys.forEach {
                tab.add(ItemStack(this, 1, it))
            }
        }
    }

    override fun initCapabilities(stack: ItemStack, nbt: NBTTagCompound?): ICapabilityProvider? {
        capabilityProvider?.let { return it(InitCapabilitiesArgs(stack, nbt)) }
        return super.initCapabilities(stack, nbt)
    }

    override fun hitEntity(stack: ItemStack, target: EntityLivingBase, attacker: EntityLivingBase): Boolean {
        onHitEntity?.let { return it(HitEntityArgs(stack, target, attacker)) }
        return super.hitEntity(stack, target, attacker)
    }

    override fun toString(): String {
        return "ItemBase($registryName)"
    }
}

data class HitEntityArgs(val stack: ItemStack, val target: EntityLivingBase, val attacker: EntityLivingBase)
data class InitCapabilitiesArgs(val stack: ItemStack, val nbt: NBTTagCompound?)