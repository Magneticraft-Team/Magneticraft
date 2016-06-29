package com.cout970.magneticraft.item

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemCrushedOre : ItemBase("crushed_ore") {

    override val variants = mapOf(
        0 to "iron",
        1 to "gold",
        2 to "copper",
        3 to "lead",
        4 to "cobalt",
        5 to "tungsten"
    )

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return super.getUnlocalizedName(stack) + "." + variants[stack?.metadata]
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for (i in variants.keys) {
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}