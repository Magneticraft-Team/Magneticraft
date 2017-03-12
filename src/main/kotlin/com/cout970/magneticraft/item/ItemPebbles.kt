package com.cout970.magneticraft.item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/06/2016.
 */
object ItemPebbles : ItemMod("pebbles", "iron", "gold", "copper", "lead", "cobalt", "tungsten") {

    //TODO Decide on actual secondary maps

    val secondaries = mapOf(
            0 to 4,
            1 to 2,
            2 to 1,
            3 to 5,
            4 to 1,
            5 to 3
    )

    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack.metadata]?.removePrefix("ore=")}"
}