package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.fuel.IFuel
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemCrushedMisc : ItemBase("crushed_misc") {

    override val variants = mapOf(
            1 to "ore=lapis"
    )


    override fun getUnlocalizedName(stack: ItemStack?): String =
            "${unlocalizedName}_${ItemCrushedMisc.variants[stack?.metadata]?.removePrefix("ore=")}"
}

object ItemCrushedCoal : ItemBase("crushed_coal"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.crushedCoalBurnTime
}

object ItemWoodChip : ItemBase("wood_chip"), IFuel<Item> {
    override fun getBurnTime(): Int = Config.woodChipBurnTime
}