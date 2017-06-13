package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.Metals
import com.cout970.magneticraft.item.Tools
import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/03/26.
 */

var items: List<Item> = emptyList()
    private set

fun initItems() {
    val items_ = mutableListOf<Item>()

    items_ += Metals.initItems()
    items_ += Tools.initItems()

    items_.forEach { GameRegistry.register(it) }
    items = items_
}
//listOf<Item>(
//        ItemGuideBook,
//        ItemIronHammer,
//        ItemStoneHammer,
//        ItemCrushedOre,
//        ItemCrushedCoal,
//        ItemCrushedLapis,
//        ItemWoodChip,
//        ItemHandSieve,
//        ItemPulpWood,
//        ItemPebbles,
//        ItemPebblesCoal,
//        ItemPebblesLapis,
//        ItemMesh,
//        ItemIngot,
//        ItemCoilOfWire,
//        ItemHeavyPlate,
//        ItemLightPlate,
//        ItemVoltmeter,
//        ItemThermometer,
//        ItemNugget,
//        ItemBattery,
//        ItemFloppyDisk,
//        ItemCoke
//)