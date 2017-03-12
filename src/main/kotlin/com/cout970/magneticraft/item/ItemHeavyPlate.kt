package com.cout970.magneticraft.item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemHeavyPlate : ItemMod("heavy_plate", "iron", "gold", "copper", "lead", "cobalt", "tungsten") {


    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack.metadata]?.removePrefix("ore=")}"
}