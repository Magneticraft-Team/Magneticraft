package com.cout970.magneticraft.misc

import com.cout970.magneticraft.systems.integration.ItemHolder
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 13/05/2016.
 * Creates the tab for the Magneticraft items in creative inventory.
 */
object CreativeTabMg : ItemGroup("magneticraft") {

    override fun createIcon(): ItemStack = ItemHolder.guideBook?.copy() ?: ItemStack.EMPTY
}
