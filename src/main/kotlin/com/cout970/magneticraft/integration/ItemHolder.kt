package com.cout970.magneticraft.integration

import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry

/**
 * Created by cout970 on 2017/09/30.
 */
object ItemHolder {

    @JvmField
    @GameRegistry.ItemStackHolder("immersiveengineering:material", meta = 6)
    val coalCoke: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("immersiveengineering:stone_decoration", meta = 3)
    val coalCokeBlock: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("thermalfoundation:material", meta = 800)
    val sawdust: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("thermalfoundation:material", meta = 768)
    val pulverizedCoal: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("thermalfoundation:material", meta = 769)
    val pulverizedCharcoal: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("thermalfoundation:material", meta = 770)
    val pulverizedObsidian: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("mekanism:oreblock", meta = 2)
    val tinOre: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("mekanism:oreblock", meta = 0)
    val osmiumOre: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("rftools:dimensional_shard_ore", meta = 0)
    val dimensionalShardOre0: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("rftools:dimensional_shard_ore", meta = 1)
    val dimensionalShardOre1: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("rftools:dimensional_shard_ore", meta = 2)
    val dimensionalShardOre2: ItemStack? = null

    @JvmField
    @GameRegistry.ItemStackHolder("rftools:dimensional_shard", meta = 0)
    val dimensionalShard: ItemStack? = null
}