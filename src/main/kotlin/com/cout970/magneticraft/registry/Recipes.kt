package com.cout970.magneticraft.registry

import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.TableSieveRecipeManager
import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.multiblock.BlockGrinder
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.multiblock.BlockSolarPanel
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
import net.minecraftforge.oredict.ShapelessOreRecipe

/**
 * Created by cout970 on 11/06/2016.
 */

/**
 * Called by CommonProxy to register all the recipes in the mod
 */
fun registerRecipes() {
    //@formatter:off

    //CRUSHING TABLE RECIPES
    addCrushingTableRecipe(Items.SKULL.stack(meta = 4), Items.GUNPOWDER.stack(size = 8))
    addCrushingTableRecipe(Items.SKULL.stack(meta = 0), Items.DYE.stack(size = 8, meta = 15))
    addCrushingTableRecipe(Items.SKULL.stack(meta = 1), ItemPebblesCoal.stack(size = 9))
    addCrushingTableRecipe(Items.SKULL.stack(meta = 2), Items.ROTTEN_FLESH.stack(size = 4))
    addCrushingTableRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 0))
    addCrushingTableRecipe(ItemStack(Blocks.GOLD_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 1))
    addCrushingTableRecipe(ItemStack(BlockOre, 1, 0), ItemCrushedOre.stack(size = 1, meta = 2))
    addCrushingTableRecipe(ItemStack(BlockOre, 1, 1), ItemCrushedOre.stack(size = 1, meta = 3))
    addCrushingTableRecipe(ItemStack(BlockOre, 1, 2), ItemCrushedOre.stack(size = 1, meta = 4))
    addCrushingTableRecipe(ItemStack(BlockOre, 1, 3), ItemCrushedOre.stack(size = 1, meta = 5))
    addCrushingTableRecipe(ItemStack(BlockLimestone, 1, 2), ItemStack(BlockBurntLimestone, 1, 2))

    addCrushingTableRecipe(ItemStack(IRON_INGOT, 1), ItemStack(ItemLightPlate, 1, 0))
    addCrushingTableRecipe(ItemStack(GOLD_INGOT, 1), ItemStack(ItemLightPlate, 1, 1))
    addCrushingTableRecipe(ItemStack(ItemIngot, 1, 2), ItemStack(ItemLightPlate, 1, 2))
    addCrushingTableRecipe(ItemStack(ItemIngot, 1, 3), ItemStack(ItemLightPlate, 1, 3))
    addCrushingTableRecipe(ItemStack(ItemIngot, 1, 4), ItemStack(ItemLightPlate, 1, 4))
    addCrushingTableRecipe(ItemStack(ItemIngot, 1, 5), ItemStack(ItemLightPlate, 1, 5))

    //UTILITY CRUSHING TABLE RECIPES - ITEMS
    addCrushingTableRecipe(ItemStack(BLAZE_ROD), ItemStack(BLAZE_POWDER, 3))
    addCrushingTableRecipe(ItemStack(BONE), ItemStack(DYE, 4, 15))
    addCrushingTableRecipe(ItemStack(Blocks.LAPIS_ORE, 1), ItemCrushedLapis.stack(size = 4))
    addCrushingTableRecipe(ItemStack(Blocks.COAL_ORE, 1), ItemCrushedCoal.stack(size = 2))

    //UTILITY CRUSHING TABLE RECIPES - BLOCKS
    addCrushingTableRecipe(ItemStack(Blocks.STONE), ItemStack(Blocks.COBBLESTONE))
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 6), ItemStack(Blocks.STONE, 1, 5))
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 4), ItemStack(Blocks.STONE, 1, 3))
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 2), ItemStack(Blocks.STONE, 1, 1))
    addCrushingTableRecipe(ItemStack(Blocks.STONEBRICK), ItemStack(Blocks.STONEBRICK, 1, 2))
    addCrushingTableRecipe(ItemStack(Blocks.STONEBRICK, 1, 1), ItemStack(Blocks.MOSSY_COBBLESTONE))
    addCrushingTableRecipe(ItemStack(Blocks.RED_SANDSTONE, 1, 2), ItemStack(Blocks.RED_SANDSTONE))
    addCrushingTableRecipe(ItemStack(Blocks.SANDSTONE, 1, 2), ItemStack(Blocks.SANDSTONE))
    addCrushingTableRecipe(ItemStack(Blocks.PRISMARINE, 1, 1), ItemStack(Blocks.PRISMARINE))
    addCrushingTableRecipe(ItemStack(Blocks.END_BRICKS, 1), ItemStack(Blocks.END_STONE, 1))

    //HYDRAULIC PRESS RECIPES
    addHydraulicPressRecipe(ItemStack(IRON_INGOT, 2), ItemStack(ItemHeavyPlate, 1, 0), 120f)
    addHydraulicPressRecipe(ItemStack(GOLD_INGOT, 2), ItemStack(ItemHeavyPlate, 1, 1), 50f)
    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 2), ItemStack(ItemHeavyPlate, 1, 2), 100f)
    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 3), ItemStack(ItemHeavyPlate, 1, 3), 50f)
    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 4), ItemStack(ItemHeavyPlate, 1, 4), 120f)
    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 5), ItemStack(ItemHeavyPlate, 1, 5), 150f)

    addHydraulicPressRecipe(ItemStack(BlockLimestone, 4, 2), ItemStack(BlockBurntLimestone, 4, 2), 50f)

    //UTILITY HYDRAUILIC PRESS RECIPES - BLOCKS
    addHydraulicPressRecipe(ItemStack(Blocks.STONE), ItemStack(Blocks.COBBLESTONE), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 6), ItemStack(Blocks.STONE, 1, 5), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 4), ItemStack(Blocks.STONE, 1, 3), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 2), ItemStack(Blocks.STONE, 1, 1), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.STONEBRICK, 1, 1), ItemStack(Blocks.MOSSY_COBBLESTONE), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.STONEBRICK), ItemStack(Blocks.STONEBRICK, 1, 2), 55f)
    addHydraulicPressRecipe(ItemStack(Blocks.END_BRICKS), ItemStack(Blocks.END_STONE, 1, 2), 100f)
    addHydraulicPressRecipe(ItemStack(Blocks.RED_SANDSTONE, 1, 2), ItemStack(Blocks.RED_SANDSTONE), 40f)
    addHydraulicPressRecipe(ItemStack(Blocks.SANDSTONE, 1, 2), ItemStack(Blocks.SANDSTONE), 40f)
    addHydraulicPressRecipe(ItemStack(Blocks.PRISMARINE, 1, 1), ItemStack(Blocks.PRISMARINE), 50f)

    addHydraulicPressRecipe(ItemStack(Blocks.ICE), ItemStack(Blocks.PACKED_ICE), 200f)

    //UTILITY HYDRAUILIC PRESS RECIPES - ITEMS
    addHydraulicPressRecipe(ItemStack(Items.REEDS, 2), ItemStack(PAPER), 30f)
    addHydraulicPressRecipe(ItemStack(ItemResource, 2), ItemStack(PAPER), 25f)

    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 0), ItemStack(ItemLightPlate, 1, 0), 120f)
    addHydraulicPressRecipe(ItemStack(GOLD_NUGGET, 6), ItemStack(ItemLightPlate, 1, 1), 50f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 2), ItemStack(ItemLightPlate, 1, 2), 100f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 3), ItemStack(ItemLightPlate, 1, 3), 50f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 4), ItemStack(ItemLightPlate, 1, 4), 120f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 5), ItemStack(ItemLightPlate, 1, 5), 150f)

    addHydraulicPressRecipe(ItemStack(BlockWoodChip), ItemStack(BlockFiberboard, 4), 50f)

    //GRINDER RECIPES
    addGrinderRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 0), ItemStack(Blocks.GRAVEL), 1f, 5f)
    addGrinderRecipe(ItemStack(Blocks.GOLD_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 1), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
    addGrinderRecipe(ItemStack(BlockOre, 1, 0), ItemCrushedOre.stack(size = 1, meta = 2), ItemStack(Blocks.GRAVEL), 0.15f, 100f)
    addGrinderRecipe(ItemStack(BlockOre, 1, 1), ItemCrushedOre.stack(size = 1, meta = 3), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
    addGrinderRecipe(ItemStack(BlockOre, 1, 2), ItemCrushedOre.stack(size = 1, meta = 4), ItemStack(Blocks.GRAVEL), 0.15f, 120f)
    addGrinderRecipe(ItemStack(BlockOre, 1, 3), ItemCrushedOre.stack(size = 1, meta = 5), ItemStack(Blocks.GRAVEL), 0.15f, 150f)

    addGrinderRecipe(ItemStack(Blocks.REDSTONE_ORE, 1), ItemStack(Items.REDSTONE, 4), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
    addGrinderRecipe(ItemStack(Blocks.LAPIS_ORE, 1), ItemCrushedLapis.stack(size = 4), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
    addGrinderRecipe(ItemStack(Blocks.COAL_ORE, 1), ItemCrushedCoal.stack(size = 2), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
    addGrinderRecipe(ItemStack((Blocks.LOG)), ItemWoodChip.stack(size = 16), 45f)

    //CRAFTING RECIPES

    //nuggets
    addRecipe(ItemStack(ItemNugget, 9, 0), "I", 'I', "ingotIron")
    addRecipe(ItemStack(ItemNugget, 9, 2), "I", 'I', "ingotCopper")
    addRecipe(ItemStack(ItemNugget, 9, 3), "I", 'I', "ingotLead")
    addRecipe(ItemStack(ItemNugget, 9, 4), "I", 'I', "ingotCobalt")
    addRecipe(ItemStack(ItemNugget, 9, 5), "I", 'I', "ingotTungsten")
    //blocks
    addRecipe(ItemStack(BlockCompactedCopper), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 2))
    addRecipe(ItemStack(BlockCompactedLead), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 3))
    addRecipe(ItemStack(BlockCompactedCobalt), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 4))
    addRecipe(ItemStack(BlockCompactedTungsten), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 5))
    addRecipe(ItemStack(BlockWoodChip), "iii", "iii", "iii", 'i', ItemStack(ItemWoodChip))
    //other
    addRecipe(ItemStack(ItemResource, 8), "III", "IXI", "III", 'I', ItemStack(ItemWoodChip), 'X', "listAllWater")
    addRecipe(ItemStack(ItemIronHammer), true, "XX#", "XZX", "#Z#", 'X', of(IRON_INGOT), 'Z', of(STICK))
    addRecipe(ItemStack(ItemStoneHammer), true, "XX#", "XZX", "#Z#", 'X', of(COBBLESTONE), 'Z', of(STICK))
    addRecipe(ItemStack(BlockLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockLimestone))
    addRecipe(ItemStack(BlockTileLimestone, 4), "XY", "YX", 'X', ItemStack(BlockLimestone), 'Y', ItemStack(BlockBurntLimestone))
    addRecipe(ItemStack(BlockBurntLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockBurntLimestone))
//    addRecipe(ItemStack(ItemGuideBook), "CB", 'C', "ingotCopper", 'B', of(BOOK))
    addRecipe(ItemStack(BlockIncendiaryGenerator), "ICI", "IFI", "IBI", 'I', "ingotIron", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
    addRecipe(ItemStack(ItemCoilOfWire), "#C#", "C#C", "#C#", 'C', "ingotCopper")
    addRecipe(ItemStack(ItemVoltmeter), "WIW", "PRP", "CPC", 'C', "ingotCopper", 'I', "ingotIron", 'P', of(PLANKS), 'R', ItemCoilOfWire, 'W', of(WOOL))
    addRecipe(ItemStack(BlockElectricFurnace), "CCC", "CFC", "CBC", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
    addRecipe(ItemStack(BlockInfiniteWater), "IBI", "TCT", "IBI", 'C', "ingotCobalt", 'I', "ingotIron", 'T', "ingotTungsten", 'B', Items.WATER_BUCKET)
    addRecipe(ItemStack(BlockMachineBlock, 2), "SSS", "I#I", "PIP", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockHydraulicPress), "PPP", "IMI", "SSS", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron", 'M', BlockMachineBlock)
    addRecipe(ItemStack(BlockGrinder), "PPP", "IWI", "MMM", 'I', "lightPlateLead", 'M', BlockMachineBlock, 'P', "heavyPlateIron", 'W', ItemCoilOfWire)
    addRecipe(ItemStack(BlockMachineBlockSupportColumn, 4), "PSP", "OSO", "PSP", 'O', "lightPlateLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockStripedMachineBlock, 8), "YSB", "S#S", "BSY", 'Y', "dyeYellow", 'S', BlockBurntLimestone, 'B', "dyeBlack")
    addRecipe(ItemStack(BlockBattery), "ILI", "CPC", "LCL", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'L', "lightPlateLead", 'C', "ingotCobalt")
    addRecipe(ItemStack(BlockElectricPoleAdapter), "INI", "IPI", "ICI", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'N', BlockElectricConnector, 'C', "ingotCopper")
    addRecipe(ItemStack(BlockElectricConnector, 8), "#C#", "WLW", "SSS", 'C', "ingotCopper", 'W', of(WOOL), 'S', of(STONE_SLAB), 'L', "ingotLead")
    addRecipe(ItemStack(BlockAirLock), "TCT", "PSP", "TCT", 'C', "heavyPlateCobalt", 'T', "ingotTungsten", 'P', ItemCoilOfWire, 'S', of(SPONGE))
    addRecipe(ItemStack(BlockTableSieve), "SPS", "I#I", "WWW", 'W', of(PLANKS), 'I', "ingotIron", 'P', of(PAPER), 'S', of(WOODEN_SLAB))
    addRecipe(ItemStack(BlockCrushingTable), "SSS", "WWW", "W#W", 'S', of(STONE_SLAB), 'W', of(LOG))
    addRecipe(ItemStack(BlockFeedingTrough), "M#M", "SWS", 'S', of(STICK), 'W', of(PLANKS), 'M', of(WOODEN_SLAB))
    addRecipe(ItemStack(BlockElectricPole), "CPC", "#W#", "#W#", 'P', of(PLANKS), 'C', BlockElectricConnector, 'W', of(LOG))
    addRecipe(ItemStack(BlockSolarPanel), "LCL", "I#I", "IPI", 'L', "lightPlateLead", 'C', ItemCoilOfWire, 'I', "lightPlateIron", 'P', "heavyPlateIron")
    addRecipe(ItemStack(BlockElectricalMachineBlock), "III", "PWP", "III",  'I', "ingotIron", 'P', "lightPlateIron", 'W', ItemCoilOfWire)
    addRecipe(ItemStack(BlockCoke), "XXX", "XXX", "XXX", 'X', of(ItemCoke))
    addRecipe(ItemStack(ItemCoke, 9), "###", "#X#", "###", 'X', of(BlockCoke))

    //SMELTING RECIPES
    addSmeltingRecipe(ItemStack(BlockBurntLimestone, 1, 0), ItemStack(BlockLimestone, 1, 0))
    addSmeltingRecipe(ItemStack(BlockBurntLimestone, 1, 2), ItemStack(BlockLimestone, 1, 2))
    //ores
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 2), ItemStack(BlockOre, 1, 0))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 3), ItemStack(BlockOre, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 4), ItemStack(BlockOre, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 5), ItemStack(BlockOre, 1, 3))
    //pebbles
    addSmeltingRecipe(ItemStack(Items.IRON_INGOT, 2, 0), ItemStack(ItemPebbles, 1, 0))
    addSmeltingRecipe(ItemStack(Items.GOLD_INGOT, 2, 0), ItemStack(ItemPebbles, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 2), ItemStack(ItemPebbles, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 3), ItemStack(ItemPebbles, 1, 3))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 4), ItemStack(ItemPebbles, 1, 4))
    addSmeltingRecipe(ItemStack(ItemIngot, 2, 5), ItemStack(ItemPebbles, 1, 5))
    //crushed ores
    addSmeltingRecipe(ItemStack(Items.IRON_INGOT, 1, 0), ItemStack(ItemCrushedOre, 1, 0))
    addSmeltingRecipe(ItemStack(Items.GOLD_INGOT, 1, 0), ItemStack(ItemCrushedOre, 1, 1))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 2), ItemStack(ItemCrushedOre, 1, 2))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 3), ItemStack(ItemCrushedOre, 1, 3))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 4), ItemStack(ItemCrushedOre, 1, 4))
    addSmeltingRecipe(ItemStack(ItemIngot, 1, 5), ItemStack(ItemCrushedOre, 1, 5))
    //charcoal
    addSmeltingRecipe(ItemStack(Items.COAL, 1, 1), ItemStack(BlockWoodChip))

    //TABLE SIEVE RECIPES
    for (i in ItemPebbles.variants.keys) {
        addTableSieveRecipe(ItemStack(ItemCrushedOre, 1, i), ItemStack(ItemPebbles, 1, i), ItemStack(COBBLESTONE), 0.15f)
    }
    addTableSieveRecipe(ItemStack(ItemCrushedLapis), ItemStack(ItemPebblesLapis), ItemStack(DYE, 1, 4), 0.1f)
    addTableSieveRecipe(ItemStack(ItemCrushedCoal), ItemStack(ItemPebblesCoal), ItemStack(DIAMOND), 0.001f)
    addTableSieveRecipe(ItemStack(Blocks.GRAVEL), ItemStack(Items.FLINT), ItemStack(Items.FLINT), 0.15f)

    //@formatter:on
}

private fun addRecipe(result: ItemStack, vararg craft: Any) {
    GameRegistry.addRecipe(ShapedOreRecipe(result, *craft))
}

private fun addShapelessRecipe(result: ItemStack, vararg craft: Any) {
    GameRegistry.addRecipe(ShapelessOreRecipe(result, *craft))
}

private fun addSmeltingRecipe(result: ItemStack, input: ItemStack) {
    GameRegistry.addSmelting(input, result, 0.1f) // i don't care about xp
}

private fun addCrushingTableRecipe(input: ItemStack, output: ItemStack) {
    CrushingTableRecipeManager.registerRecipe(CrushingTableRecipeManager.createRecipe(input, output, true))
}

private fun addTableSieveRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float) {
    TableSieveRecipeManager.registerRecipe(TableSieveRecipeManager.createRecipe(input, output0, output1, prob, true))
}

private fun addTableSieveRecipe(input: ItemStack, output: ItemStack) {
    TableSieveRecipeManager.registerRecipe(TableSieveRecipeManager.createRecipe(input, output, output, 0f, true))
}

private fun addHydraulicPressRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
    HydraulicPressRecipeManager.registerRecipe(HydraulicPressRecipeManager.createRecipe(input, output, ticks, true))
}

private fun addGrinderRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float, ticks: Float) {
    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output0, output1, prob, ticks, true))
}

private fun addGrinderRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output, output, 0f, ticks, true))
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