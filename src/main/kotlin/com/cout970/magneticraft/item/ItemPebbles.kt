package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebbles : ItemBase("pebbles") {

    val PEBBLES = mapOf(0 to "iron", 1 to "gold", 2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")

    init{
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return super.getUnlocalizedName(stack)+"."+ PEBBLES[stack?.metadata]
    }

    override fun getModels(): Map<Int, ModelResourceLocation> {
        val map = mutableMapOf<Int, ModelResourceLocation>()
        PEBBLES.entries.forEach { map.put(it.key, ModelResourceLocation("${registryName}_${PEBBLES[it.key]}", "inventory")) }
        return map
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for(i in PEBBLES.keys){
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}