package item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 24/06/2016.
 */
object ItemIngot : ItemMod("ingot", "ingot_copper", "ingot_lead", "ingot_cobalt", "ingot_tungsten") {

    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${ItemIngot.variants[stack.metadata].removePrefix("ingot_")}"
}