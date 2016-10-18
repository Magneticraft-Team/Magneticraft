package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.*
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import com.cout970.magneticraft.item.hammers.ItemStoneHammer
import net.minecraftforge.fml.common.registry.GameRegistry

//List with all the items in the mod
val items = listOf(
        ItemGuideBook,
        ItemIronHammer,
        ItemStoneHammer,
        ItemCrushedOre,
        ItemCrushedMisc,
        ItemHandSieve,
        ItemResource,
        ItemPebbles,
        ItemPebblesMisc,
        ItemIngot,
        ItemCoilOfWire,
        ItemHeavyPlate,
        ItemLightPlate,
        ItemVoltmeter,
        ItemNugget,
        ItemBattery,
        ItemCoke
)

/**
 * Registers all the items, called by CommonProxy
 */
fun registerItems() {
    items.forEach {
        GameRegistry.register(it)
    }
}