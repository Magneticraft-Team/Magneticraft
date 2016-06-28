package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 24/06/2016.
 */
object ItemIngots : ItemBase("ingots") {

    override val variants = mapOf(
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