package item

import com.teamwizardry.librarianlib.common.base.item.ItemMod
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 2016/09/06.
 */
object ItemNugget  : ItemMod("nugget", "nugget_iron", "nugget_copper", "nugget_lead", "nugget_cobalt", "nugget_tungsten") {

    override fun getUnlocalizedName(stack: ItemStack): String =
            "${unlocalizedName}_${variants[stack.metadata].removePrefix("nugget_")}"
}