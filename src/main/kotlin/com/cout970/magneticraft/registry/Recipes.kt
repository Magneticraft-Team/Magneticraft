package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.sluicebox.SluiceBoxRecipeManager
import com.cout970.magneticraft.block.Decoration
import com.cout970.magneticraft.block.Ores
import com.cout970.magneticraft.item.Crafting
import com.cout970.magneticraft.item.Metals
import com.cout970.magneticraft.misc.inventory.stack
import net.minecraft.init.Blocks
import net.minecraft.init.Blocks.COBBLESTONE
import net.minecraft.init.Items
import net.minecraft.init.Items.*
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.registry.GameRegistry


/**
 * Created by cout970 on 11/06/2016.
 * Modified by Yurgen
 * Called by CommonProxy to register all the recipes in the mod
 */
fun registerRecipes() {


    //CRUSHING TABLE RECIPES
    // skulls
    addCrushingTableRecipe(Items.SKULL.stack(meta = 4), Items.GUNPOWDER.stack(8))
    addCrushingTableRecipe(Items.SKULL.stack(meta = 0), Items.DYE.stack(8, 15))
    addCrushingTableRecipe(Items.SKULL.stack(meta = 2), Items.ROTTEN_FLESH.stack(4))
    // ores
    addCrushingTableRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), Metals.chunks.stack(1, 0))
    addCrushingTableRecipe(ItemStack(Blocks.GOLD_ORE, 1, 0), Metals.chunks.stack(1, 1))
    addCrushingTableRecipe(ItemStack(Ores.ores, 1, 0), Metals.chunks.stack(1, 2))
    addCrushingTableRecipe(ItemStack(Ores.ores, 1, 1), Metals.chunks.stack(1, 3))
    addCrushingTableRecipe(ItemStack(Ores.ores, 1, 2), Metals.chunks.stack(1, 4))
    addCrushingTableRecipe(ItemStack(Ores.ores, 1, 3), Metals.chunks.stack(1, 5))
    addCrushingTableRecipe(ItemStack(Ores.ores, 1, 4), Crafting.crafting.stack(1, Crafting.meta["sulfur"]!!))
    // limestone
    addCrushingTableRecipe(ItemStack(Decoration.limestone, 1, 0), Decoration.limestone.stack(1, 2))
    addCrushingTableRecipe(ItemStack(Decoration.burnLimestone, 1, 0), Decoration.burnLimestone.stack(1, 2))
    // light plates
    addCrushingTableRecipe(ItemStack(IRON_INGOT, 1), Metals.lightPlates.stack(1, 0))
    addCrushingTableRecipe(ItemStack(GOLD_INGOT, 1), Metals.lightPlates.stack(1, 1))
    addCrushingTableRecipe(ItemStack(Metals.ingots, 1, 0), Metals.lightPlates.stack(1, 2))
    addCrushingTableRecipe(ItemStack(Metals.ingots, 1, 1), Metals.lightPlates.stack(1, 3))
    addCrushingTableRecipe(ItemStack(Metals.ingots, 1, 2), Metals.lightPlates.stack(1, 4))
    addCrushingTableRecipe(ItemStack(Metals.ingots, 1, 3), Metals.lightPlates.stack(1, 5))
    addCrushingTableRecipe(ItemStack(Metals.ingots, 1, 4), Metals.lightPlates.stack(1, 6))
    // rods
    addCrushingTableRecipe(ItemStack(BLAZE_ROD), BLAZE_POWDER.stack(5))
    addCrushingTableRecipe(ItemStack(BONE), DYE.stack(4, 15))
    // blocks
    addCrushingTableRecipe(ItemStack(Blocks.STONE), Blocks.COBBLESTONE.stack())
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 6), Blocks.STONE.stack(1, 5))
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 4), Blocks.STONE.stack(1, 3))
    addCrushingTableRecipe(ItemStack(Blocks.STONE, 1, 2), Blocks.STONE.stack(1, 1))
    addCrushingTableRecipe(ItemStack(Blocks.STONEBRICK), Blocks.STONEBRICK.stack(1, 2))
    addCrushingTableRecipe(ItemStack(Blocks.STONEBRICK, 1, 1), Blocks.MOSSY_COBBLESTONE.stack())
    addCrushingTableRecipe(ItemStack(Blocks.RED_SANDSTONE, 1, 2), Blocks.RED_SANDSTONE.stack())
    addCrushingTableRecipe(ItemStack(Blocks.SANDSTONE, 1, 2), Blocks.SANDSTONE.stack())
    addCrushingTableRecipe(ItemStack(Blocks.PRISMARINE, 1, 1), Blocks.PRISMARINE.stack())
    addCrushingTableRecipe(ItemStack(Blocks.END_BRICKS, 1), Blocks.END_STONE.stack(1))

    //@formatter:off
//    //HYDRAULIC PRESS RECIPES
//    addHydraulicPressRecipe(ItemStack(IRON_INGOT, 2), ItemStack(ItemHeavyPlate, 1, 0), 120f)
//    addHydraulicPressRecipe(ItemStack(GOLD_INGOT, 2), ItemStack(ItemHeavyPlate, 1, 1), 50f)
//    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 2), ItemStack(ItemHeavyPlate, 1, 2), 100f)
//    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 3), ItemStack(ItemHeavyPlate, 1, 3), 50f)
//    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 4), ItemStack(ItemHeavyPlate, 1, 4), 120f)
//    addHydraulicPressRecipe(ItemStack(ItemIngot, 2, 5), ItemStack(ItemHeavyPlate, 1, 5), 150f)
//    addHydraulicPressRecipe(ItemStack(BlockLimestone, 4, 2), ItemStack(BlockBurntLimestone, 4, 2), 50f)
//
//    //UTILITY HYDRAUILIC PRESS RECIPES - BLOCKS
//    addHydraulicPressRecipe(ItemStack(Blocks.STONE), ItemStack(Blocks.COBBLESTONE), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 6), ItemStack(Blocks.STONE, 1, 5), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 4), ItemStack(Blocks.STONE, 1, 3), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.STONE, 1, 2), ItemStack(Blocks.STONE, 1, 1), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.STONEBRICK, 1, 1), ItemStack(Blocks.MOSSY_COBBLESTONE), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.STONEBRICK), ItemStack(Blocks.STONEBRICK, 1, 2), 55f)
//    addHydraulicPressRecipe(ItemStack(Blocks.END_BRICKS), ItemStack(Blocks.END_STONE, 1, 2), 100f)
//    addHydraulicPressRecipe(ItemStack(Blocks.RED_SANDSTONE, 1, 2), ItemStack(Blocks.RED_SANDSTONE), 40f)
//    addHydraulicPressRecipe(ItemStack(Blocks.SANDSTONE, 1, 2), ItemStack(Blocks.SANDSTONE), 40f)
//    addHydraulicPressRecipe(ItemStack(Blocks.PRISMARINE, 1, 1), ItemStack(Blocks.PRISMARINE), 50f)
//    addHydraulicPressRecipe(ItemStack(Blocks.ICE), ItemStack(Blocks.PACKED_ICE), 200f)
//
//    //UTILITY HYDRAUILIC PRESS RECIPES - ITEMS
//    addHydraulicPressRecipe(ItemStack(Items.REEDS, 2), ItemStack(PAPER), 30f)
//    addHydraulicPressRecipe(ItemStack(ItemPulpWood, 2), ItemStack(PAPER), 25f)
//    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 0), ItemStack(ItemLightPlate, 1, 0), 120f)
//    addHydraulicPressRecipe(ItemStack(GOLD_NUGGET, 6), ItemStack(ItemLightPlate, 1, 1), 50f)
//    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 2), ItemStack(ItemLightPlate, 1, 2), 100f)
//    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 3), ItemStack(ItemLightPlate, 1, 3), 50f)
//    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 4), ItemStack(ItemLightPlate, 1, 4), 120f)
//    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 5), ItemStack(ItemLightPlate, 1, 5), 150f)
//    addHydraulicPressRecipe(ItemStack(BlockWoodChip), ItemStack(BlockFiberboard, 4), 50f)
//
//    //KILN RECIPES
//    addKilnRecipe(ItemStack(COAL_BLOCK), BlockCoke.defaultState, 50, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
//    addKilnRecipe(ItemStack(Blocks.LOG, 1, 0), BlockCharcoalSlab.defaultState, 25, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
//    addKilnRecipe(ItemStack(Blocks.SAND), Blocks.GLASS.defaultState, 25, GLASS_MAKING_TEMP, QUARTZ_MELTING_POINT)
//    addKilnRecipe(ItemStack(Blocks.CLAY), Blocks.HARDENED_CLAY.defaultState, 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)
//    addKilnRecipe(ItemStack(BlockFluxedGravel), BlockGlazedBrick.defaultState, 25, FURNACE_BRICK_TEMP, QUARTZ_MELTING_POINT)
//    addKilnRecipe(ItemStack(Blocks.SPONGE, 1, 1), ItemStack(Blocks.SPONGE, 1, 0), 25, WATER_BOILING_POINT, COKE_REACTION_TEMP)
//
//    //KILN SHELF RECIPES
//    addKilnRecipe(ItemStack(COAL, 1, 0), ItemStack(ItemCoke), 50, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
//    addKilnRecipe(ItemStack(CLAY_BALL), ItemStack(BRICK), 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)
//    addKilnRecipe(ItemStack(CHORUS_FRUIT), ItemStack(CHORUS_FRUIT_POPPED, 1, 0), 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)
//
//    //GRINDER RECIPES
//    addGrinderRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 0), ItemStack(Blocks.GRAVEL), 1f, 5f)
//    //TODO: Remove iron placeholder values
//    addGrinderRecipe(ItemStack(Blocks.GOLD_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 1), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
//    addGrinderRecipe(ItemStack(BlockOre, 1, 0), ItemCrushedOre.stack(size = 1, meta = 2), ItemStack(Blocks.GRAVEL), 0.15f, 100f)
//    addGrinderRecipe(ItemStack(BlockOre, 1, 1), ItemCrushedOre.stack(size = 1, meta = 3), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
//    addGrinderRecipe(ItemStack(BlockOre, 1, 2), ItemCrushedOre.stack(size = 1, meta = 4), ItemStack(Blocks.GRAVEL), 0.15f, 120f)
//    addGrinderRecipe(ItemStack(BlockOre, 1, 3), ItemCrushedOre.stack(size = 1, meta = 5), ItemStack(Blocks.GRAVEL), 0.15f, 150f)
//    addGrinderRecipe(ItemStack(Blocks.REDSTONE_ORE, 1), ItemStack(Items.REDSTONE, 4), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
//    addGrinderRecipe(ItemStack(Blocks.LAPIS_ORE, 1), ItemCrushedLapis.stack(size = 4), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
//    addGrinderRecipe(ItemStack(Blocks.COAL_ORE, 1), ItemCrushedCoal.stack(size = 2), ItemStack(Blocks.GRAVEL), 0.15f, 50f)
//    addGrinderRecipe(ItemStack((Blocks.LOG)), ItemWoodChip.stack(size = 16), 45f)
//
//    //CRAFTING RECIPES
//
//    addStampedPartRecipe(ItemStack(Items.MINECART), "I#I", "III", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Items.CAULDRON), "I#I", "I#I", "III", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Items.BUCKET), "I#I", "#I#", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Items.IRON_DOOR, 3), "II", "II", "II", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), "II", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Blocks.IRON_TRAPDOOR), "II", "II", 'I', "lightPlateIron")
//    addStampedPartRecipe(ItemStack(Blocks.HOPPER), "I#I", "ICI", "#I#", 'I', "lightPlateIron", 'C', "chestWood")
//    addStampedPartRecipe(ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), "II", 'I', "lightPlateGold")
//    addStampedRecipe(ItemStack(Items.SHEARS), "#I", "I#", 'I', "lightPlateIron")
//    addStampedRecipe(ItemStack(Items.IRON_SWORD), "I", "I", "S", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.IRON_AXE), "II", "IS", "#S", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.IRON_HOE), "II", "#S", "#S", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.IRON_SHOVEL), "I", "S", "S", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.IRON_PICKAXE), "III", "#S#", "#S#", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.IRON_BOOTS), "I#I", "I#I", 'I', "lightPlateIron")
//    addStampedRecipe(ItemStack(Items.IRON_HELMET), "III", "I#I", 'I', "lightPlateIron")
//    addStampedRecipe(ItemStack(Items.IRON_LEGGINGS), "III", "I#I", "I#I", 'I', "lightPlateIron")
//    addStampedRecipe(ItemStack(Items.IRON_CHESTPLATE), "I#I", "III", "III", 'I', "lightPlateIron")
//    addStampedRecipe(ItemStack(Items.SHIELD), "PIP", "PPP", "#P#", 'I', "lightPlateIron", 'P', "plankWood")
//    addStampedRecipe(ItemStack(ItemIronHammer), "II#", "ISI", "#S#", 'I', "lightPlateIron", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_SWORD), "I", "I", "S", 'I', "lightPlateGold", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_AXE), "II", "IS", "#S", 'I', "lightPlateGold", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_HOE), "II", "#S", "#S", 'I', "lightPlateGold", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_SHOVEL), "I", "S", "S", 'I', "lightPlateGold", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_PICKAXE), "III", "#S#", "#S#", 'I', "lightPlateGold", 'S', "stickWood")
//    addStampedRecipe(ItemStack(Items.GOLDEN_BOOTS), "I#I", "I#I", 'I', "lightPlateGold")
//    addStampedRecipe(ItemStack(Items.GOLDEN_HELMET), "III", "I#I", 'I', "lightPlateGold")
//    addStampedRecipe(ItemStack(Items.GOLDEN_LEGGINGS), "III", "I#I", "I#I", 'I', "lightPlateGold")
//    addStampedRecipe(ItemStack(Items.GOLDEN_CHESTPLATE), "I#I", "III", "III", 'I', "lightPlateGold")
//    addHeavyRecipe(ItemStack(Items.IRON_BOOTS), "I#I", "I#I", 'I', "heavyPlateIron")
//    addHeavyRecipe(ItemStack(Items.IRON_HELMET), "III", "I#I", 'I', "heavyPlateIron")
//    addHeavyRecipe(ItemStack(Items.IRON_LEGGINGS), "III", "I#I", "I#I", 'I', "heavyPlateIron")
//    addHeavyRecipe(ItemStack(Items.IRON_CHESTPLATE), "I#I", "III", "III", 'I', "heavyPlateIron")
//    addHeavyRecipe(ItemStack(Items.GOLDEN_BOOTS), "I#I", "I#I", 'I', "heavyPlateGold")
//    addHeavyRecipe(ItemStack(Items.GOLDEN_HELMET), "III", "I#I", 'I', "heavyPlateGold")
//    addHeavyRecipe(ItemStack(Items.GOLDEN_LEGGINGS), "III", "I#I", "I#I", 'I', "heavyPlateGold")
//    addHeavyRecipe(ItemStack(Items.GOLDEN_CHESTPLATE), "I#I", "III", "III", 'I', "heavyPlateGold")
//    addHeavyRecipe(ItemStack(Items.SHIELD), "PIP", "PPP", "#P#", 'I', "heavyPlateIron", 'P', "plankWood")
//
//    //nuggets
//    addRecipe(ItemStack(ItemNugget, 9, 0), "I", 'I', "ingotIron")
//    addRecipe(ItemStack(ItemNugget, 9, 2), "I", 'I', "ingotCopper")
//    addRecipe(ItemStack(ItemNugget, 9, 3), "I", 'I', "ingotLead")
//    addRecipe(ItemStack(ItemNugget, 9, 4), "I", 'I', "ingotCobalt")
//    addRecipe(ItemStack(ItemNugget, 9, 5), "I", 'I', "ingotTungsten")
//    //blocks
//    addRecipe(ItemStack(BlockCompactedCopper), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 2))
//    addRecipe(ItemStack(BlockCompactedLead), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 3))
//    addRecipe(ItemStack(BlockCompactedCobalt), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 4))
//    addRecipe(ItemStack(BlockCompactedTungsten), "iii", "iii", "iii", 'i', ItemStack(ItemIngot, 1, 5))
//    addRecipe(ItemStack(BlockWoodChip), "iii", "iii", "iii", 'i', ItemStack(ItemWoodChip))
//    //other
//    addRecipe(ItemStack(ItemIronHammer), true, "PX#", "XZX", "#Z#", 'P', "lightPlateIron", 'X', "ingotIron", 'Z', "stickWood")
//    addRecipe(ItemStack(ItemStoneHammer), true, "XX#", "XZX", "#Z#", 'X', "cobblestone", 'Z', "stickWood")
//
//    addRecipe(ItemStack(BlockFluxedGravel, 4), "CSC", "LGF", "CSC", 'G', "gravel", 'C', Items.CLAY_BALL, 'L', "pebblesLead", 'F', "pebblesCobalt_Mgc", 'S', "sand")
//
//    addRecipe(ItemStack(ItemPulpWood, 8), "III", "IXI", "III", 'I', ItemStack(ItemWoodChip), 'X', "listAllWater")
//    addRecipe(ItemStack(BlockLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockLimestone))
//    addRecipe(ItemStack(BlockTileLimestone, 4), "XY", "YX", 'X', ItemStack(BlockLimestone), 'Y', ItemStack(BlockBurntLimestone))
//    addRecipe(ItemStack(BlockBurntLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockBurntLimestone))
////    addRecipe(ItemStack(ItemGuideBook), "CB", 'C', "ingotCopper", 'B', of(BOOK))
//    addRecipe(ItemStack(BlockIncendiaryGenerator), "ICI", "IFI", "IBI", 'I', "ingotIron", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
//
//    addRecipe(ItemStack(ItemCoilOfWire), "#C#", "C#C", "#C#", 'C', "ingotCopper")
//    addRecipe(ItemStack(ItemMesh), "CCC", "CCC", "CCC", 'C', ItemStack(Blocks.IRON_BARS))
//    addRecipe(ItemStack(ItemVoltmeter), "WIW", "PRP", "CPC", 'C', "ingotCopper", 'I', "ingotIron", 'P', "plankWood", 'R', ItemCoilOfWire, 'W', of(WOOL))
//    addRecipe(ItemStack(ItemThermometer), "GIG", "PRP", "TPT", 'T', "ingotTungsten", 'I', "ingotIron", 'P', "plankWood", 'R', ItemCoilOfWire, 'G', "blockGlass")
//
//    addRecipe(ItemStack(BlockElectricFurnace), "CCC", "CFC", "CBC", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
//    addRecipe(ItemStack(BlockInfiniteWater), "IBI", "TCT", "IBI", 'C', "ingotCobalt", 'I', "ingotIron", 'T', "ingotTungsten", 'B', Items.WATER_BUCKET)
//
//    addRecipe(ItemStack(BlockMachineBlock, 2), "SSS", "I#I", "PIP", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
//    addRecipe(ItemStack(BlockMesh, 3), "SPS", "MMM", "SPS", 'M', ItemMesh, 'S', BlockBurntLimestone, 'P', "lightPlateIron")
//    addRecipe(ItemStack(BlockHydraulicPress), "PPP", "IMI", "SSS", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron", 'M', BlockMachineBlock)
//    addRecipe(ItemStack(BlockSifter), "PHP", "IMI", "SSS", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron", 'M', BlockMachineBlock, 'H', Blocks.HOPPER)
//    addRecipe(ItemStack(BlockGrinder), "PPP", "IWI", "MMM", 'I', "lightPlateLead", 'M', BlockMachineBlock, 'P', "heavyPlateIron", 'W', ItemCoilOfWire)
//    addRecipe(ItemStack(BlockMachineBlockSupportColumn, 4), "PSP", "OSO", "PSP", 'O', "lightPlateLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
//    addRecipe(ItemStack(BlockStripedMachineBlock, 8), "YSB", "S#S", "BSY", 'Y', "dyeYellow", 'S', BlockBurntLimestone, 'B', "dyeBlack")
//
//    addRecipe(ItemStack(BlockBattery), "ILI", "CPC", "LCL", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'L', "lightPlateLead", 'C', "ingotCobalt")
//    addRecipe(ItemStack(BlockElectricPoleAdapter), "INI", "IPI", "ICI", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'N', BlockElectricConnector, 'C', "ingotCopper")
//    addRecipe(ItemStack(BlockElectricConnector, 8), "#C#", "WLW", "SSS", 'C', "ingotCopper", 'W', of(WOOL), 'S', of(STONE_SLAB), 'L', "ingotLead")
//    addRecipe(ItemStack(BlockAirLock), "TCT", "PSP", "TCT", 'C', "heavyPlateCobalt", 'T', "ingotTungsten", 'P', ItemCoilOfWire, 'S', of(SPONGE))
//
//    addRecipe(ItemStack(BlockCrushingTable), "SSS", "WWW", "W#W", 'S', of(STONE_SLAB), 'W', "logWood")
//    addRecipe(ItemStack(BlockKiln), "BDB", "BPB", 'B', BlockBurntLimestone, 'P', "lightPlateCopper", 'D', Items.IRON_DOOR)
//    addRecipe(ItemStack(BlockKilnShelf), "BPB", "B#B", 'B', of(IRON_BARS), 'P', "lightPlateIron")
//    addRecipe(ItemStack(BlockFeedingTrough), "M#M", "SWS", 'S', "stickWood", 'W', "plankWood", 'M', "slabWood")
//
//    addRecipe(ItemStack(BlockElectricPole), "CPC", "#W#", "#W#", 'P', "plankWood", 'C', BlockElectricConnector, 'W', "logWood")
//    addRecipe(ItemStack(BlockSolarPanel), "LCL", "I#I", "IPI", 'L', "lightPlateLead", 'C', ItemCoilOfWire, 'I', "lightPlateIron", 'P', "heavyPlateIron")
//    addRecipe(ItemStack(BlockElectricalMachineBlock), "III", "PWP", "III",  'I', "ingotIron", 'P', "lightPlateIron", 'W', ItemCoilOfWire)
//
//    addRecipe(ItemStack(BlockElectricHeater), "XPX", "XFX", "XFX", 'P', "lightPlateCopper", 'F', ItemCoilOfWire, 'X', "ingotBrick")
//    addRecipe(ItemStack(BlockBrickFurnace), "XXX", "XFX", "XPX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', Blocks.FURNACE)
//    addRecipe(ItemStack(BlockFirebox), "XPX", "XFX", "XXX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', Blocks.FURNACE)
//    addRecipe(ItemStack(BlockIcebox), "XXX", "XFX", "XPX", 'X', "cobblestone", 'P', "lightPlateCopper", 'F', Items.CAULDRON)
//    addRecipe(ItemStack(BlockHeatReservoir), "XPX", "XFX", "XXX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', BlockCompactedCopper)
//    addRecipe(ItemStack(BlockHeatSink), "PPP", "XPX", 'X', "ingotBrick", 'P', "lightPlateCopper")
//    addRecipe(ItemStack(BlockHeatPipe, 8), "BBB", "PIP", "BBB", 'B', "ingotBrick", 'P', "lightPlateCopper", 'I', "ingotCopper")
//    addRecipe(ItemStack(BlockRedstoneHeatPipe), "C", "P", 'P', BlockHeatPipe, 'C', Items.REPEATER)
//
//    //why? "Found an itemStack with a null item. This is an error from another mod."
////    addRecipe(ItemStack(BlockThermometer), "#C#", "IPD", 'C', Items.COMPARATOR, 'I', "ingotTungsten", 'D', "ingotIron", 'P', BlockHeatPipe)
//
//    addRecipe(ItemStack(BlockCoke), "XXX", "XXX", "XXX", 'X', of(ItemCoke))
//    addRecipe(ItemStack(ItemCoke, 9), "###", "#X#", "###", 'X', of(BlockCoke))
//
//    //ICEBOX RECIPES
//    addIceboxRecipeWater(ItemStack(Items.SNOWBALL), 125, false)
//    addIceboxRecipeWater(ItemStack(Blocks.SNOW), 500, false)
//    addIceboxRecipeWater(ItemStack(Blocks.ICE), 900, true)
//    addIceboxRecipeWater(ItemStack(Blocks.PACKED_ICE), 1000, false)

    //SMELTING RECIPES
    addSmeltingRecipe(ItemStack(Decoration.burnLimestone, 1, 0), ItemStack(Decoration.limestone, 1, 0))
    addSmeltingRecipe(ItemStack(Decoration.burnLimestone, 1, 2), ItemStack(Decoration.limestone, 1, 2))
    //ores
    addSmeltingRecipe(ItemStack(Metals.ingots, 1, 0), ItemStack(Ores.ores, 1, 0))
    addSmeltingRecipe(ItemStack(Metals.ingots, 1, 1), ItemStack(Ores.ores, 1, 1))
    addSmeltingRecipe(ItemStack(Metals.ingots, 1, 2), ItemStack(Ores.ores, 1, 2))
    addSmeltingRecipe(ItemStack(Metals.ingots, 1, 3), ItemStack(Ores.ores, 1, 3))
    //crushed ores
    addSmeltingRecipe(ItemStack(Items.IRON_INGOT, 2, 0), ItemStack(Metals.chunks, 1, 0))
    addSmeltingRecipe(ItemStack(Items.GOLD_INGOT, 2, 0), ItemStack(Metals.chunks, 1, 1))
    addSmeltingRecipe(ItemStack(Metals.ingots, 2, 0), ItemStack(Metals.chunks, 1, 2))
    addSmeltingRecipe(ItemStack(Metals.ingots, 2, 1), ItemStack(Metals.chunks, 1, 3))
    addSmeltingRecipe(ItemStack(Metals.ingots, 2, 2), ItemStack(Metals.chunks, 1, 4))
    addSmeltingRecipe(ItemStack(Metals.ingots, 2, 3), ItemStack(Metals.chunks, 1, 5))

    //SLUICE BOX RECIPES
    Metals.chunks.variants.keys.forEach { meta ->
        addSluiceBoxRecipe(Metals.chunks.stack(meta = meta), Metals.dusts.stack(meta = meta), listOf(COBBLESTONE.stack() to 0.15f))
    }

    addSluiceBoxRecipe(Blocks.GRAVEL.stack(), Items.FLINT.stack(), listOf(Items.FLINT.stack() to 0.15f))
    addSluiceBoxRecipe(Blocks.SAND.stack(), ItemStack.EMPTY,
            listOf(
                    Items.GOLD_NUGGET.stack() to 0.01f,
                    Items.GOLD_NUGGET.stack() to 0.005f,
                    Items.GOLD_NUGGET.stack() to 0.0025f,
                    Items.GOLD_NUGGET.stack() to 0.00125f,
                    Items.GOLD_NUGGET.stack() to 0.000625f,
                    Items.GOLD_NUGGET.stack() to 0.0003125f,
                    Items.GOLD_NUGGET.stack() to 0.00015625f,
                    Items.GOLD_NUGGET.stack() to 0.000078125f,
                    Items.GOLD_NUGGET.stack() to 0.0000390625f
            ))
    //@formatter:on
}


private fun addSmeltingRecipe(result: ItemStack, input: ItemStack) {
    GameRegistry.addSmelting(input, result, 0.1f) // i don't care about xp
}

private fun addCrushingTableRecipe(input: ItemStack, output: ItemStack) {
    CrushingTableRecipeManager.registerRecipe(CrushingTableRecipeManager.createRecipe(input, output, true))
}

//
//private fun addHeatExchangerRecipe(input: FluidStack, output: FluidStack, heat: Long, minTemp: Double, maxTemp: Double,
//                                   reverseLow: Boolean, reverseHigh: Boolean) {
//    HeatExchangerRecipeManager.registerRecipe(
//            HeatExchangerRecipeManager.createRecipe(input, output, heat, minTemp, maxTemp, reverseLow, reverseHigh))
//}
private fun addSluiceBoxRecipe(input: ItemStack, output: ItemStack,
                               otherOutput: List<Pair<ItemStack, Float>> = emptyList()) {
    SluiceBoxRecipeManager.registerRecipe(SluiceBoxRecipeManager.createRecipe(input, output, otherOutput, true))
}
//
//private fun addSifterRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob1: Float, output2: ItemStack,
//                            prob2: Float, duration: Float) {
//    SifterRecipeManager.registerRecipe(
//            SifterRecipeManager.createRecipe(input, output0, output1, prob1, output2, prob2, duration, true))
//}
//
//private fun addSifterRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob1: Float, duration: Float) {
//    SifterRecipeManager.registerRecipe(
//            SifterRecipeManager.createRecipe(input, output0, output1, prob1, output1, 0f, duration, true))
//}
//
//private fun addSifterRecipe(input: ItemStack, output0: ItemStack, duration: Float) {
//    SifterRecipeManager.registerRecipe(
//            SifterRecipeManager.createRecipe(input, output0, output0, 0f, output0, 0f, duration, true))
//}
//
//private fun addHydraulicPressRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
//    HydraulicPressRecipeManager.registerRecipe(HydraulicPressRecipeManager.createRecipe(input, output, ticks, true))
//}
//
//private fun addKilnRecipe(input: ItemStack, output: ItemStack, duration: Int, minTemp: Double, maxTemp: Double) {
//    KilnRecipeManager.registerRecipe(KilnRecipeManager.createRecipe(input, output, duration, minTemp, maxTemp, true))
//}
//
//private fun addKilnRecipe(input: ItemStack, output: IBlockState, duration: Int, minTemp: Double, maxTemp: Double) {
//    KilnRecipeManager.registerRecipe(KilnRecipeManager.createRecipe(input, output, duration, minTemp, maxTemp, true))
//}
//
//private fun addIceboxRecipe(input: ItemStack, output: FluidStack, heat: Long, specificHeat: Double, minTemp: Double,
//                            maxTemp: Double, reverse: Boolean) {
//    IceboxRecipeManager.registerRecipe(
//            IceboxRecipeManager.createRecipe(input, output, heat, specificHeat, minTemp, maxTemp, reverse))
//}
//
//private fun addIceboxRecipeWater(input: ItemStack, output: Int, reverse: Boolean) {
//    IceboxRecipeManager.registerRecipe(IceboxRecipeManager.createRecipe(input, FluidStack(FluidRegistry.WATER, output),
//            (WATER_HEAT_OF_FUSION * output / 1000).toLong(), WATER_HEAT_CAPACITY, WATER_MELTING_POINT,
//            WATER_BOILING_POINT, reverse))
//}
//
//private fun addGrinderRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float, ticks: Float) {
//    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output0, output1, prob, ticks, true))
//}
//
//private fun addGrinderRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
//    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output, output, 0f, ticks, true))
//}
//
//private fun addStampedRecipe(result: ItemStack, vararg craft: Any) {
//    addEnchantRecipe(result, "Stamped", listOf(Pair(Enchantments.UNBREAKING, 1)), *craft)
//}
//
//private fun addStampedPartRecipe(result: ItemStack, vararg craft: Any) {
//    addLoreRecipe(result, "Stamped", *craft)
//}
//
//private fun addHeavyRecipe(result: ItemStack, vararg craft: Any) {
//    addEnchantRecipe(result, "Heavy Duty", listOf(Pair(Enchantments.UNBREAKING, 3), Pair(Enchantments.PROTECTION, 1)),*craft)
//}
//
////function to get the first ore dictionary entry for the block if exist, or the block if not exist
//private fun of(i: Block): Any {
//    val ids = OreDictionary.getOreIDs(ItemStack(i))
//    if (ids.isEmpty()) return i
//    return OreDictionary.getOreName(ids.first())
//}
//
////function to get the first ore dictionary entry for the item if exist, or the item if not exist
//private fun of(i: Item): Any {
//    val ids = OreDictionary.getOreIDs(ItemStack(i))
//    if (ids.isEmpty()) return i
//    return OreDictionary.getOreName(ids.first())
//}