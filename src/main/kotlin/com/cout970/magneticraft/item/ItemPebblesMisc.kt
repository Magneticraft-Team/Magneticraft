package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import net.minecraft.item.Item

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebblesLapis : ItemBase("lapis_pebbles") {
}


object ItemPebblesCoal : ItemBase("coal_pebbles"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.coalPebbleBurnTime
}