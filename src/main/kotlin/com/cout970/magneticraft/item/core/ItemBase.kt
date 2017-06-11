package com.cout970.magneticraft.item.core

import com.cout970.magneticraft.MOD_ID
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

/**
 * Created by cout970 on 2017/06/11.
 */
class ItemBase : Item() {

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

    override fun toString(): String {
        return "ItemBase($registryName)"
    }
}