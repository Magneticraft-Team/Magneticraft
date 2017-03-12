package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.fuel.IFuel
import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.Item

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemCrushedLapis : ItemMod("crushed_lapis")

object ItemCrushedCoal : ItemMod("crushed_coal"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.crushedCoalBurnTime
}

object ItemWoodChip : ItemMod("wood_chip"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.woodChipBurnTime
}