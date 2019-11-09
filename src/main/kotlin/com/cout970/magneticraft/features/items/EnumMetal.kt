package com.cout970.magneticraft.features.items

import com.cout970.magneticraft.misc.inventory.stack
import net.minecraft.block.Blocks
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

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
                IRON -> listOf(Blocks.IRON_ORE.stack())
                else -> listOf(Blocks.GOLD_ORE.stack())
            }
        }
        // TODO
//        if (this == ALUMINIUM) {
//            return (OreDictionary.getOres("oreAluminium") + OreDictionary.getOres("oreAluminum"))
//                .map { it.withSize(1) }
//        }
//        return OreDictionary.getOres("ore" + name.toLowerCase().capitalize())
//            .map { it.withSize(1) }
        return emptyList()
    }

    fun getIngot(): ItemStack {
        return when (this) {
            IRON -> Items.IRON_INGOT.stack()
            GOLD -> Items.GOLD_INGOT.stack()
            COPPER -> MetallicItems.copperIngot.stack()
            LEAD -> MetallicItems.leadIngot.stack()
            COBALT -> MetallicItems.cobaltIngot.stack()
            TUNGSTEN -> MetallicItems.tungstenIngot.stack()
            STEEL -> MetallicItems.steelIngot.stack()
            ALUMINIUM -> MetallicItems.aluminiumIngot.stack()
            MITHRIL -> MetallicItems.mithrilIngot.stack()
            NICKEL -> MetallicItems.nickelIngot.stack()
            OSMIUM -> MetallicItems.osmiumIngot.stack()
            SILVER -> MetallicItems.silverIngot.stack()
            TIN -> MetallicItems.tinIngot.stack()
            ZINC -> MetallicItems.zincIngot.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getNugget(): ItemStack {
        return when (this) {
            IRON -> Items.IRON_NUGGET.stack()
            GOLD -> Items.GOLD_NUGGET.stack()
            COPPER -> MetallicItems.copperNugget.stack()
            LEAD -> MetallicItems.leadNugget.stack()
            COBALT -> MetallicItems.cobaltNugget.stack()
            TUNGSTEN -> MetallicItems.tungstenNugget.stack()
            STEEL -> MetallicItems.steelNugget.stack()
            ALUMINIUM -> MetallicItems.aluminiumNugget.stack()
            MITHRIL -> MetallicItems.mithrilNugget.stack()
            NICKEL -> MetallicItems.nickelNugget.stack()
            OSMIUM -> MetallicItems.osmiumNugget.stack()
            SILVER -> MetallicItems.silverNugget.stack()
            TIN -> MetallicItems.tinNugget.stack()
            ZINC -> MetallicItems.zincNugget.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getDust(): ItemStack {
        return when (this) {
            IRON -> MetallicItems.ironDust.stack()
            GOLD -> MetallicItems.goldDust.stack()
            COPPER -> MetallicItems.copperDust.stack()
            LEAD -> MetallicItems.leadDust.stack()
            COBALT -> MetallicItems.cobaltDust.stack()
            TUNGSTEN -> MetallicItems.tungstenDust.stack()
            STEEL -> MetallicItems.steelDust.stack()
            ALUMINIUM -> MetallicItems.aluminiumDust.stack()
            MITHRIL -> MetallicItems.mithrilDust.stack()
            NICKEL -> MetallicItems.nickelDust.stack()
            OSMIUM -> MetallicItems.osmiumDust.stack()
            SILVER -> MetallicItems.silverDust.stack()
            TIN -> MetallicItems.tinDust.stack()
            ZINC -> MetallicItems.zincDust.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getLightPlate(): ItemStack {
        return when (this) {
            IRON -> MetallicItems.ironLightPlate.stack()
            GOLD -> MetallicItems.goldLightPlate.stack()
            COPPER -> MetallicItems.copperLightPlate.stack()
            LEAD -> MetallicItems.leadLightPlate.stack()
            TUNGSTEN -> MetallicItems.tungstenLightPlate.stack()
            STEEL -> MetallicItems.steelLightPlate.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getHeavyPlate(): ItemStack {
        return when (this) {
            IRON -> MetallicItems.ironHeavyPlate.stack()
            GOLD -> MetallicItems.goldHeavyPlate.stack()
            COPPER -> MetallicItems.copperHeavyPlate.stack()
            LEAD -> MetallicItems.leadHeavyPlate.stack()
            TUNGSTEN -> MetallicItems.tungstenHeavyPlate.stack()
            STEEL -> MetallicItems.steelHeavyPlate.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getChunk(): ItemStack {
        return when (this) {
            IRON -> MetallicItems.ironChunk.stack()
            GOLD -> MetallicItems.goldChunk.stack()
            COPPER -> MetallicItems.copperChunk.stack()
            LEAD -> MetallicItems.leadChunk.stack()
            COBALT -> MetallicItems.cobaltChunk.stack()
            TUNGSTEN -> MetallicItems.tungstenChunk.stack()
            ALUMINIUM -> MetallicItems.aluminiumChunk.stack()
            MITHRIL -> MetallicItems.mithrilChunk.stack()
            NICKEL -> MetallicItems.nickelChunk.stack()
            OSMIUM -> MetallicItems.osmiumChunk.stack()
            SILVER -> MetallicItems.silverChunk.stack()
            TIN -> MetallicItems.tinChunk.stack()
            ZINC -> MetallicItems.zincChunk.stack()
            else -> ItemStack.EMPTY
        }
    }

    fun getRockyChunk(): ItemStack {
        return when (this) {
            IRON -> MetallicItems.ironRockyChunk.stack()
            GOLD -> MetallicItems.goldRockyChunk.stack()
            COPPER -> MetallicItems.copperRockyChunk.stack()
            LEAD -> MetallicItems.leadRockyChunk.stack()
            COBALT -> MetallicItems.cobaltRockyChunk.stack()
            TUNGSTEN -> MetallicItems.tungstenRockyChunk.stack()
            ALUMINIUM -> MetallicItems.aluminiumRockyChunk.stack()
            MITHRIL -> MetallicItems.mithrilRockyChunk.stack()
            NICKEL -> MetallicItems.nickelRockyChunk.stack()
            OSMIUM -> MetallicItems.osmiumRockyChunk.stack()
            SILVER -> MetallicItems.silverRockyChunk.stack()
            TIN -> MetallicItems.tinRockyChunk.stack()
            ZINC -> MetallicItems.zincRockyChunk.stack()
            else -> ItemStack.EMPTY
        }
    }
}