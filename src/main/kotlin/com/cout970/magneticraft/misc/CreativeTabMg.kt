package com.cout970.magneticraft.misc

import com.cout970.magneticraft.features.items.CraftingItems
import net.minecraft.creativetab.CreativeTabs
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
        return t("itemGroup." + this.tabLabel)
    }

    override fun getTabIconItem(): ItemStack = ItemStack(CraftingItems.guideBook)
}
