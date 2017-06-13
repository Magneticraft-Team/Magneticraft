package com.cout970.magneticraft.item

import com.cout970.magneticraft.api.tool.IHammer
import com.cout970.magneticraft.item.core.HitEntityArgs
import com.cout970.magneticraft.item.core.IItemMaker
import com.cout970.magneticraft.item.core.ItemBase
import com.cout970.magneticraft.item.core.ItemBuilder
import com.cout970.magneticraft.misc.CreativeTabMg
import com.cout970.magneticraft.registry.ITEM_HAMMER
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.DamageSource
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider

/**
 * Created by cout970 on 2017/06/12.
 */
object Tools : IItemMaker {

    lateinit var stoneHammer: ItemBase private set
    lateinit var ironHammer: ItemBase private set
    lateinit var steelHammer: ItemBase private set

    override fun initItems(): List<Item> {
        val builder = ItemBuilder().apply {
            creativeTab = CreativeTabMg
            isFull3d = true
            maxStackSize = 1
        }

        stoneHammer = builder.withName("stone_hammer").apply {
            onHitEntity = createHitEntity(2.0f)
            capabilityProvider = { Hammer(8) }
            maxDamage = 130
        }.build()
        ironHammer = builder.withName("iron_hammer").apply {
            onHitEntity = createHitEntity(3.5f)
            capabilityProvider = { Hammer(10) }
            maxDamage = 250
        }.build()
        steelHammer = builder.withName("steel_hammer").apply {
            onHitEntity = createHitEntity(5.0f)
            capabilityProvider = { Hammer(15) }
            maxDamage = 750
        }.build()

        return listOf(stoneHammer, ironHammer, steelHammer)
    }

    private fun createHitEntity(damage: Float): (HitEntityArgs) -> Boolean {
        return {
            it.stack.damageItem(2, it.attacker)
            it.target.attackEntityFrom(DamageSource.GENERIC, damage)
        }
    }

    class Hammer(val damage: Int) : IHammer, ICapabilityProvider {

        override fun getBreakingSpeed(): Int = damage

        override fun applyDamage(item: ItemStack, player: EntityPlayer): ItemStack {
            item.damageItem(1, player)
            return item
        }

        override fun <T : Any?> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
            @Suppress("UNCHECKED_CAST")
            return this as T
        }

        override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean = capability == ITEM_HAMMER
    }
}