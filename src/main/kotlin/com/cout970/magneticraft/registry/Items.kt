package com.cout970.magneticraft.registry

import com.cout970.magneticraft.item.*
import net.minecraft.item.Item
import net.minecraftforge.fml.common.registry.ForgeRegistries

/**
 * Created by cout970 on 2017/03/26.
 */

var items: List<Item> = emptyList()
    private set

fun initItems() {
    val items_ = mutableListOf<Item>()

    items_ += MetallicItems.initItems()
    items_ += ToolItems.initItems()
    items_ += ElectricItems.initItems()
    items_ += CraftingItems.initItems()
    items_ += ComputerItems.initItems()
    items_ += Upgrades.initItems()

    items_.forEach { ForgeRegistries.ITEMS.register(it) }
    items = items_
}