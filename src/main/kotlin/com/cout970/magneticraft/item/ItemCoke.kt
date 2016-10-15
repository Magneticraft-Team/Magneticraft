package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.FuelProvider
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.item.Item

object ItemCoke : ItemBase("coke"), FuelProvider<Item> {
    init {
        creativeTab = CreativeTabMg

    }

    override fun getBurnTime(): Int = Config.cokeBurnTime
}