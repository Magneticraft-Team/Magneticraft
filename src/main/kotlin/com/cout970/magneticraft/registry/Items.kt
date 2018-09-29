package com.cout970.magneticraft.registry

import com.cout970.magneticraft.features.items.*
import net.minecraft.item.Item
import net.minecraftforge.registries.IForgeRegistry

/**
 * Created by cout970 on 2017/03/26.
 */

var items: List<Item> = emptyList()
    private set

fun initItems(registry: IForgeRegistry<Item>) {
    val itemList = mutableListOf<Item>()

    itemList += MetallicItems.initItems()
    itemList += ToolItems.initItems()
    itemList += ElectricItems.initItems()
    itemList += CraftingItems.initItems()
    itemList += ComputerItems.initItems()
    itemList += Upgrades.initItems()

    itemList.forEach { registry.register(it) }
    blocks.forEach { (_, itemBlock) -> itemBlock?.let { registry.register(it) } }
    items = itemList
}