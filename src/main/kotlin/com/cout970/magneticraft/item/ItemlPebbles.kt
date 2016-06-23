package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemlPebbles : ItemBase("metal_pebble") {

    val METAL_PEBBLES = mapOf(0 to "iron", 1 to "gold", 2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")

    init{
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return super.getUnlocalizedName(stack)+"."+ METAL_PEBBLES[stack?.metadata]
    }

    override fun getMaxModels(): Int = METAL_PEBBLES.size

    override fun getModelLoc(i: Int): ModelResourceLocation {
        return ModelResourceLocation("${registryName}_${METAL_PEBBLES[i]}", "inventory")
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for(i in METAL_PEBBLES.keys){
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}