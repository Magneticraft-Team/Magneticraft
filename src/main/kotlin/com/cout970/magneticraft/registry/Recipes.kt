package com.cout970.magneticraft.registry

import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRecipe
import com.cout970.magneticraft.api.registries.machines.tablesieve.TableSieveRegistry
import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.item.*
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import com.cout970.magneticraft.item.hammers.ItemStoneHammer
import net.minecraft.block.Block
import net.minecraft.init.Blocks
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
    CrushingTableRegistry.registerRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 0))
    CrushingTableRegistry.registerRecipe(ItemStack(Blocks.GOLD_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 1))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 0), ItemCrushedOre.stack(size = 1, meta = 2))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 1), ItemCrushedOre.stack(size = 1, meta = 3))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 2), ItemCrushedOre.stack(size = 1, meta = 4))
    CrushingTableRegistry.registerRecipe(ItemStack(BlockOre, 1, 3), ItemCrushedOre.stack(size = 1, meta = 5))


    //CRAFTING RECIPES
    addRecipe(ItemStack(BlockCrushingTable), "SSS", "WWW", "W#W", 'S', of(STONE), 'W', of(LOG))
    addRecipe(ItemStack(ItemIronHammer), true, "XX#", "XZX", "#Z#", 'X', of(IRON_INGOT), 'Z', of(STICK))
    addRecipe(ItemStack(ItemStoneHammer), true, "XX#", "XZX", "#Z#", 'X', of(COBBLESTONE), 'Z', of(STICK))
    addRecipe(ItemStack(BlockLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockLimestone, 1, 0))
    addRecipe(ItemStack(BlockTileLimestone, 4, 0), "XY", "YX", 'X', ItemStack(BlockLimestone, 1, 0), 'Y', ItemStack(BlockBurntLimestone, 1, 0))
    addRecipe(ItemStack(BlockBurntLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockBurntLimestone, 1, 0))
    addRecipe(ItemStack(ItemGuideBook), "CB", 'C', "ingotCopper", 'B', of(BOOK))
    addRecipe(ItemStack(BlockFeedingTrough), "S#S", "W#W", "SWS", 'S', of(STICK), 'W', of(PLANKS))
    addRecipe(ItemStack(BlockElectricConnector, 8), "#C#", "LCL", "SSS", 'S', of(STONE), 'C', "ingotCopper", 'L', of(WOOL))
    addRecipe(ItemStack(BlockIncendiaryGenerator), "ICI", "IFI", "IBI", 'I', "ingotIron", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
    addRecipe(ItemStack(BlockElectricPole), "CPC", "#W#", "#W#", 'P', of(PLANKS), 'C', "ingotCopper", 'W', of(LOG))
    addRecipe(ItemStack(BlockElectricPoleAdapter), "#C#", "IWI", "ICI", 'I', "ingotIron", 'C', "ingotCopper", 'W', ItemCoilOfWire)
    addRecipe(ItemStack(ItemCoilOfWire), "#C#", "C#C", "#C#", 'C', "ingotCopper")
    addRecipe(ItemStack(BlockTableSieve), "PIP", "W#W", "PPP", 'I', "ingotIron", 'P', of(PLANKS), 'W', of(LOG))
    addRecipe(ItemStack(ItemVoltmeter), "WIW", "PRP", "CPC", 'C', "ingotCopper", 'I', "ingotIron", 'P', of(PLANKS), 'R', ItemCoilOfWire, 'W', of(WOOL))
    addRecipe(ItemStack(BlockElectricFurnace), "CCC", "CFC", "CBC", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
    addRecipe(ItemStack(BlockBattery), "IIC", "KLK", "LKL", 'C', "ingotCopper", 'I', "ingotIron", 'K', "ingotCobalt", 'L', "ingotLead")
    addRecipe(ItemStack(BlockInfiniteWater), "IBI", "TCT", "IBI", 'C', "ingotCobalt", 'I', "ingotIron", 'T', "ingotTungsten", 'B', Items.WATER_BUCKET)

    //SMELTING RECIPES
    addSmeltingRecipe(ItemStack(BlockBurntLimestone, 1, 0), ItemStack(BlockLimestone, 1, 0))
    addSmeltingRecipe(ItemStack(BlockBurntLimestone, 1, 2), ItemStack(BlockLimestone, 1, 2))
    //ores
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 2), ItemStack(BlockOre, 1, 0))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 3), ItemStack(BlockOre, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 4), ItemStack(BlockOre, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 5), ItemStack(BlockOre, 1, 3))
    //pebles
    addSmeltingRecipe(ItemStack(Items.IRON_INGOT, 2, 0), ItemStack(ItemPebbles, 1, 0))
    addSmeltingRecipe(ItemStack(Items.GOLD_INGOT, 2, 0), ItemStack(ItemPebbles, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 2), ItemStack(ItemPebbles, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 3), ItemStack(ItemPebbles, 1, 3))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 4), ItemStack(ItemPebbles, 1, 4))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 5), ItemStack(ItemPebbles, 1, 5))
    //crushed ores
    addSmeltingRecipe(ItemStack(Items.IRON_INGOT, 2, 0), ItemStack(ItemCrushedOre, 1, 0))
    addSmeltingRecipe(ItemStack(Items.GOLD_INGOT, 2, 0), ItemStack(ItemCrushedOre, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 2), ItemStack(ItemCrushedOre, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 3), ItemStack(ItemCrushedOre, 1, 3))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 4), ItemStack(ItemCrushedOre, 1, 4))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 5), ItemStack(ItemCrushedOre, 1, 5))

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

//function to get the first ore dictionary entry for the block if exist, or the block if not exist
private fun of(i: Block): Any {
    val ids = OreDictionary.getOreIDs(ItemStack(i))
    if (ids.isEmpty()) return i
    return OreDictionary.getOreName(ids.first())
}

//function to get the first ore dictionary entry for the item if exist, or the item if not exist
private fun of(i: Item): Any {
    val ids = OreDictionary.getOreIDs(ItemStack(i))
    if (ids.isEmpty()) return i
    return OreDictionary.getOreName(ids.first())
}