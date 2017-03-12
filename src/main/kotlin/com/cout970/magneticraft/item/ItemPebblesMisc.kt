package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.fuel.IFuel
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.Item

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebblesLapis : ItemMod("lapis_pebbles")


object ItemPebblesCoal : ItemMod("coal_pebbles"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.coalPebbleBurnTime
}