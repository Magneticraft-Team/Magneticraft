package com.cout970.magneticraft.item

import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemResource : ItemBase("resource") {

    override val variants = mapOf(
            0 to "resource=pulpWood"
    )

    override fun getUnlocalizedName(stack: ItemStack?): String =
            "${unlocalizedName}_${ItemCrushedOre.variants[stack?.metadata]?.removePrefix("resource=")}"
}