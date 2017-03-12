package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.fuel.IFuel
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.Item

object ItemCoke : ItemMod("coal_coke"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.cokeBurnTime
}