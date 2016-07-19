package com.cout970.magneticraft.registry

import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRecipe
import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRegistry
import com.cout970.magneticraft.block.BlockBurntLimestone
import com.cout970.magneticraft.block.BlockCrushingTable
import com.cout970.magneticraft.block.BlockLimestone
import com.cout970.magneticraft.block.BlockOre
import com.cout970.magneticraft.item.ItemCrushedOre
import com.cout970.magneticraft.item.ItemGuideBook
import com.cout970.magneticraft.item.ItemPebbles
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import com.cout970.magneticraft.item.hammers.ItemStoneHammer
import net.minecraft.block.Block
import net.minecraft.init.Blocks.*
import net.minecraft.init.Items
import net.minecraft.init.Items.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe

/**
 * Created by cout970 on 11/06/2016.
 */

//TODO Recipe Builder
fun registerRecipes() {
    //@formatter:off

    //CRUSHING TABLE RECIPES
    CrushingTableRegistry.registerRecipe(Items.SKULL.stack(meta = 4), Items.GUNPOWDER.stack(size = 8))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 0), ItemCrushedOre.stack(size = 1, meta = 2))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 1), ItemCrushedOre.stack(size = 1, meta = 3))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 2), ItemCrushedOre.stack(size = 1, meta = 4))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 3), ItemCrushedOre.stack(size = 1, meta = 5))

    //CRAFTING RECIPES
    addRecipe(ItemStack(BlockCrushingTable), "SSS", "WWW", "W#W", 'S', of(STONE), 'W', of(LOG))
    addRecipe(ItemStack(ItemIronHammer), true, "XX#", "XZX", "#Z#", 'X', of(IRON_INGOT), 'Z', of(STICK))
    addRecipe(ItemStack(ItemStoneHammer), true, "XX#", "XZX", "#Z#", 'X', of(COBBLESTONE), 'Z', of(STICK))
    addRecipe(ItemStack(BlockLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockLimestone, 1, 0))
    addRecipe(ItemStack(BlockBurntLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockBurntLimestone, 1, 0))
    addRecipe(ItemStack(ItemGuideBook), "CB", 'C', "ingotCopper", 'B', of(BOOK))

    //TODO add a guide book recipe

    //SMELTING RECIPES
    addSmeltingRecipe(ItemStack(BlockBurntLimestone), ItemStack(BlockLimestone))

    //TABLE SIEVE RECIPES
    for (i in ItemPebbles.variants.keys) {
        addTableSieveRecipe(ItemStack(ItemCrushedOre, 1, i), ItemStack(ItemPebbles, 1, i), ItemStack(COBBLESTONE), 0.15f)
    }

    //@formatter:on
}

private fun addRecipe(result: ItemStack, vararg craft: Any) {
    GameRegistry.addRecipe(ShapedOreRecipe(result, *craft))
}

private fun addSmeltingRecipe(result: ItemStack, input: ItemStack) {
    GameRegistry.addSmelting(input, result, 0.1f) // i don't care about xp
}

private fun addTableSieveRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float) {
    TableSieveRegistry.registerRecipe(TableSieveRecipe(input, output0, output1, prob))
}

//function to get the fist ore dictionary of the block if exist, or the block
private fun of(i: Block): Any {
    val ids = OreDictionary.getOreIDs(ItemStack(i))
    if (ids.isEmpty()) return i
    return OreDictionary.getOreName(ids.first())
}

//function to get the fist ore dictionary of the item if exist, or the item
private fun of(i: Item): Any {
    val ids = OreDictionary.getOreIDs(ItemStack(i))
    if (ids.isEmpty()) return i
    return OreDictionary.getOreName(ids.first())
}