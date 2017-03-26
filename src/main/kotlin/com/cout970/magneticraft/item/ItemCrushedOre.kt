package com.cout970.magneticraft.item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemCrushedOre : ItemMod("crushed_ore", "crushed_iron", "crushed_gold", "crushed_copper", "crushed_lead", "crushed_cobalt", "crushed_tungsten") {

    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack.metadata].removePrefix("crushed_")}"
}