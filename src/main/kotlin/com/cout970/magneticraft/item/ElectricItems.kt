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

    lateinit var battery_item: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            maxStackSize = 1
        }

        battery_item = builder.withName("battery_item").copy {
            constructor = { ElectricItemBase().apply { capacity = Config.itemBatteryCapacity } }
            capabilityProvider = { ElectricItemBase.ItemEnergyCapabilityProvider(it.stack) }
        }.build()

        return listOf(battery_item)
    }
}