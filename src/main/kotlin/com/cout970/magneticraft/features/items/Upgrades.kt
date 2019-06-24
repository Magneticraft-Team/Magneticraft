package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.api.tool.IGear
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.misc.item.IItemCapability
import com.cout970.magneticraft.misc.item.ItemCapabilityProvider
import com.cout970.magneticraft.misc.resource
import com.cout970.magneticraft.registry.ITEM_GEAR
import com.cout970.magneticraft.systems.items.IItemMaker
import com.cout970.magneticraft.systems.items.ItemBase
import com.cout970.magneticraft.systems.items.ItemBuilder
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import java.util.*

/**
 * Created by cout970 on 2017/08/19.
 */

object Upgrades : IItemMaker {

    lateinit var brokenGear: ItemBase private set
    lateinit var ironGear: ItemBase private set
    lateinit var steelGear: ItemBase private set
    lateinit var tungstenGear: ItemBase private set
    lateinit var inserterUpgrade: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            isFull3d = true
            maxStackSize = 1
        }

        brokenGear = builder.withName("broken_gear").copy {
            maxDamage = 0
            customModels = listOf("normal" to resource("models/item/mcx/broken_gear.mcx"))
        }.build()

        ironGear = builder.withName("iron_gear").copy {
            capabilityProvider = { ItemCapabilityProvider(ITEM_GEAR to Gear(it.stack, 1f)) }
            maxDamage = 800
            containerItem = brokenGear
            customModels = listOf("normal" to resource("models/item/mcx/iron_gear.mcx"))
        }.build()

        steelGear = builder.withName("steel_gear").copy {
            capabilityProvider = { ItemCapabilityProvider(ITEM_GEAR to Gear(it.stack, 1.25f)) }
            maxDamage = 1600
            containerItem = brokenGear
            customModels = listOf("normal" to resource("models/item/mcx/steel_gear.mcx"))
        }.build()

        tungstenGear = builder.withName("tungsten_gear").copy {
            capabilityProvider = { ItemCapabilityProvider(ITEM_GEAR to Gear(it.stack, 1.5f)) }
            maxDamage = 3200
            containerItem = brokenGear
            customModels = listOf("normal" to resource("models/item/mcx/tungsten_gear.mcx"))
        }.build()

        inserterUpgrade = builder.withName("inserter_upgrade").copy {
            variants = mapOf(
                0 to "speed",
                1 to "stack"
            )
        }.build()

        return listOf(ironGear, brokenGear, steelGear, tungstenGear, inserterUpgrade)
    }

    class Gear(override val stack: ItemStack, val speed: Float) : IGear, IItemCapability {

        override fun getSpeedMultiplier(): Float = speed

        override fun getMaxDurability(): Int = stack.maxDamage

        override fun getDurability(): Int = stack.itemDamage

        override fun applyDamage(stack: ItemStack): ItemStack {
            stack.attemptDamageItem(1, Random(), null)
            return stack
        }
    }
}