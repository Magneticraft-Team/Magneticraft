package com.cout970.magneticraft.item

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemCrushedOre : ItemBase("crushed_ore") {

    val CRUSHED_ORES = mapOf(0 to "iron", 1 to "gold", 2 to "copper", 3 to "lead", 4 to "cobalt", 5 to "tungsten")

    init{
        hasSubtypes = true
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return super.getUnlocalizedName(stack)+"."+CRUSHED_ORES[stack?.metadata]
    }

    override fun getMaxModels(): Int = CRUSHED_ORES.size

    override fun getModelLoc(i: Int): ModelResourceLocation {
        return ModelResourceLocation("${registryName}_${CRUSHED_ORES[i]}", "inventory")
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for(i in CRUSHED_ORES.keys){
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}