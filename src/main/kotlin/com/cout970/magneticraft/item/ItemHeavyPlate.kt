package com.cout970.magneticraft.item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemHeavyPlate : ItemMod("heavy_plate", "heavy_plate_iron", "heavy_plate_gold", "heavy_plate_copper", "heavy_plate_lead", "heavy_plate_cobalt", "heavy_plate_tungsten") {


    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemCrushedOre.variants[stack.metadata].removePrefix("heavy_plate_")}"
}