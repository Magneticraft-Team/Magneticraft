package item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object ItemLightPlate : ItemMod("light_plate", "light_plate_iron", "light_plate_gold", "light_plate_copper", "light_plate_lead", "light_plate_cobalt", "light_plate_tungsten") {

    override fun getUnlocalizedName(stack: ItemStack): String =
        "${unlocalizedName}_${variants[stack.metadata].removePrefix("light_plate_")}"
}