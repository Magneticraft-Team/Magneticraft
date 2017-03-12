package com.cout970.magneticraft.item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 24/06/2016.
 */
object ItemIngot : ItemMod("ingot", "copper", "lead", "cobalt", "tungsten") {


    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack.metadata]?.removePrefix("ore=")}"
}