package com.cout970.magneticraft.misc

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 13/05/2016.
 * Creates the tab for the Magneticraft items in creative inventory.
 */
object CreativeTabMg : CreativeTabs("magneticraft") {

    override fun getTabIconItem(): ItemStack = ItemStack(Items.BOOK)
}
