package com.cout970.magneticraft.item

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebbles : ItemBase("pebbles") {

    override val variants = mapOf(
        0 to "ore=iron",
        1 to "ore=gold",
        2 to "ore=copper",
        3 to "ore=lead",
        4 to "ore=cobalt",
        5 to "ore=tungsten"
    )
    //TODO Decide on actual secondary maps

    val secondaries = mapOf(
            0 to 4,
            1 to 2,
            2 to 1,
            3 to 5,
            4 to 1,
            5 to 3
    )

    override fun getUnlocalizedName(stack: ItemStack?): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack?.metadata]?.removePrefix("ore=")}"
}