package com.cout970.magneticraft.misc

import net.minecraft.client.resources.I18n
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by cout970 on 13/05/2016.
 * Creates the tab for the Magneticraft items in creative inventory.
 */
object CreativeTabMg : CreativeTabs("magneticraft") {

    @SideOnly(Side.CLIENT)
    override fun getTranslatedTabLabel(): String {
        return I18n.format("itemGroup." + this.tabLabel)
    }

    override fun getTabIconItem(): ItemStack = ItemStack(Items.BOOK)
}
