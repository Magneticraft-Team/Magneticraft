package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.inventory.withSize
import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 2017/08/02.
 */

enum class EnumMetal(val vanilla: Boolean, val isOre: Boolean = true, val useful: Boolean = false,
                     val subComponents: List<() -> EnumMetal> = emptyList()) {

    IRON(true, useful = true),
    GOLD(true, useful = true),
    COPPER(false, useful = true),
    LEAD(false, useful = true),
    COBALT(false),
    TUNGSTEN(false, useful = true),
    STEEL(false, isOre = false, useful = true),
    ALUMINIUM(false),
    GALENA(false, subComponents = listOf({ LEAD }, { SILVER })),
    MITHRIL(false),
    NICKEL(false),
    OSMIUM(false),
    SILVER(false),
    TIN(false),
    ZINC(false);

    val isComposite: Boolean = subComponents.isNotEmpty()

    companion object {
        val subProducts = mapOf(
            IRON to listOf(NICKEL, ALUMINIUM),
            GOLD to listOf(COPPER, SILVER),
            COPPER to listOf(GOLD, IRON),
            LEAD to listOf(SILVER),
            COBALT to listOf(MITHRIL, OSMIUM),
            TUNGSTEN to listOf(IRON),
            ALUMINIUM to listOf(NICKEL, IRON),
            GALENA to listOf(LEAD, SILVER),
            MITHRIL to listOf(OSMIUM, ZINC),
            NICKEL to listOf(IRON, TIN),
            OSMIUM to listOf(MITHRIL, NICKEL),
            SILVER to listOf(LEAD),
            TIN to listOf(IRON, ALUMINIUM),
            ZINC to listOf(NICKEL, TIN)
        )
    }

    fun getOres(): List<ItemStack> {
        if (vanilla) {
            if (vanilla) return when (this) {
                IRON -> listOf(Blocks.IRON_ORE.stack(1))
                else -> listOf(Blocks.GOLD_ORE.stack(1))
            }
        }
        if (this == ALUMINIUM) {
            return (OreDictionary.getOres("oreAluminium") + OreDictionary.getOres("oreAluminum"))
                .map { it.withSize(1) }
        }
        return OreDictionary.getOres("ore" + name.toLowerCase().capitalize())
            .map { it.withSize(1) }
    }

    fun getIngot(): ItemStack {
        if (vanilla) return when (this) {
            IRON -> Items.IRON_INGOT.stack(1)
            else -> Items.GOLD_INGOT.stack(1)
        }
        return MetallicItems.ingots.stack(1, ordinal)
    }

    fun getNugget(): ItemStack {
        if (vanilla) return when (this) {
            IRON -> Items.IRON_NUGGET.stack(1)
            else -> Items.GOLD_NUGGET.stack(1)
        }
        return MetallicItems.nuggets.stack(1, ordinal)
    }

    fun getDust(): ItemStack = MetallicItems.dusts.stack(1, ordinal)

    fun getLightPlate(): ItemStack = MetallicItems.lightPlates.stack(1, ordinal)

    fun getHeavyPlate(): ItemStack = MetallicItems.heavyPlates.stack(1, ordinal)

    fun getChunk(): ItemStack {
        if (this.isComposite) return ItemStack.EMPTY
        return MetallicItems.chunks.stack(1, ordinal)
    }

    fun getRockyChunk(): ItemStack = MetallicItems.rockyChunks.stack(1, ordinal)
}