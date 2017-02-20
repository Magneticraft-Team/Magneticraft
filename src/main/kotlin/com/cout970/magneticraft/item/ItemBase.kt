package com.cout970.magneticraft.item

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.util.resource
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * This is the base class for all the items in the mod
 */
abstract class ItemBase(
    registryName: String,
    unlocalizedName: String = registryName
) : Item() {

    open val variants = mapOf(
        0 to "inventory"
    )

    override fun getHasSubtypes() = variants.size > 1

    init {
        this.registryName = resource(registryName)
        this.unlocalizedName = "${MOD_ID}.$unlocalizedName"

        creativeTab = CreativeTabMg
    }

    override fun getSubItems(itemIn: Item?, tab: CreativeTabs?, subItems: MutableList<ItemStack>?) {
        for (i in variants.keys) {
            subItems?.add(ItemStack(itemIn, 1, i))
        }
    }
}