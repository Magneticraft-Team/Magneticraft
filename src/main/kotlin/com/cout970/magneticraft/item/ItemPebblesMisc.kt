package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebblesMisc : ItemBase("pebbles_misc") {

    override val variants = mapOf(
            1 to "ore=lapis"
    )

    override fun getUnlocalizedName(stack: ItemStack?): String =
            "${unlocalizedName}_${ItemCrushedOre.variants[stack?.metadata]?.removePrefix("ore=")}"
}


object ItemCoalPebbles : ItemBase("coal_pebbles"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.coalPebbleBurnTime
}