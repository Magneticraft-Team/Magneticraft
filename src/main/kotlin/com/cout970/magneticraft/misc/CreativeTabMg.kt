package com.cout970.magneticraft.misc

import com.cout970.magneticraft.item.ItemGuideBook
import com.teamwizardry.librarianlib.common.base.ModCreativeTab
import net.minecraft.item.Item

/**
 * Created by cout970 on 13/05/2016.
 * Creates the tab for the Magneticraft items in creative inventory.
 */
object CreativeTabMg : ModCreativeTab("magneticraft") {

    init {
        registerDefaultTab()
    }
    override fun getTabIconItem(): Item? = ItemGuideBook
}
