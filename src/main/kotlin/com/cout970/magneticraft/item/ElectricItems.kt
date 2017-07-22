package com.cout970.magneticraft.item

import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.item.core.ElectricItemBase
import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import net.minecraft.item.Item

/**
 * Created by cout970 on 2017/07/02.
 */
object ElectricItems : IItemMaker {

    lateinit var battery_item_low: ItemBase private set
    lateinit var battery_item_medium: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            maxStackSize = 1
        }

        battery_item_low = builder.withName("battery_item_low").copy {
            constructor = { ElectricItemBase().apply { capacity = Config.batteryItemLowCapacity } }
            capabilityProvider = { ElectricItemBase.ItemEnergyCapabilityProvider(it.stack) }
        }.build()

        battery_item_medium = builder.withName("battery_item_medium").copy {
            constructor = { ElectricItemBase().apply { capacity = Config.batteryItemMediumCapacity } }
            capabilityProvider = { ElectricItemBase.ItemEnergyCapabilityProvider(it.stack) }
        }.build()

        return listOf(battery_item_low, battery_item_medium)
    }
}