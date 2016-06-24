package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 24/06/2016.
 */
object ItemIngots : ItemBase("ingots") {

    val INGOTS = mapOf(2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")

    init {
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return super.getUnlocalizedName(stack) + "." + INGOTS[stack?.metadata]
    }

    override fun getModels(): Map<Int, ModelResourceLocation> {
        val map = mutableMapOf<Int, ModelResourceLocation>()
        INGOTS.entries.forEach { map.put(it.key, ModelResourceLocation("${registryName}_${INGOTS[it.key]}", "inventory")) }
        return map
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for (i in INGOTS.keys) {
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}