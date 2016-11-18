package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.item.Item

object ItemCoke : ItemBase("coal_coke"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.cokeBurnTime
}