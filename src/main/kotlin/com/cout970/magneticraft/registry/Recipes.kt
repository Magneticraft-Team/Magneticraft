package com.cout970.magneticraft.registry


import com.cout970.magneticraft.api.internal.registries.machines.crushingtable.CrushingTableRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.grinder.GrinderRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.heatrecipes.HeatExchangerRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.heatrecipes.IceboxRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.hydraulicpress.HydraulicPressRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.kiln.KilnRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.sifter.SifterRecipeManager
import com.cout970.magneticraft.api.internal.registries.machines.tablesieve.TableSieveRecipeManager
import com.cout970.magneticraft.block.*
import com.cout970.magneticraft.block.decoration.*
import com.cout970.magneticraft.block.fuel.BlockCharcoalSlab
import com.cout970.magneticraft.block.fuel.BlockCoke
import com.cout970.magneticraft.block.heat.*
import com.cout970.magneticraft.block.multiblock.*
import com.cout970.magneticraft.item.*
import com.cout970.magneticraft.item.hammers.ItemIronHammer
import com.cout970.magneticraft.item.hammers.ItemStoneHammer
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.util.*
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.enchantment.Enchantment
import net.minecraft.init.Blocks
import net.minecraft.init.Blocks.*
import net.minecraft.init.Enchantments
import net.minecraft.init.Items
import net.minecraft.init.Items.*
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.oredict.OreDictionary
import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraftforge.oredict.ShapelessOreRecipe

/**
 * Created by cout970 on 11/06/2016.
 * Modified by Yurgen
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
    addHydraulicPressRecipe(ItemStack(ItemPulpWood, 2), ItemStack(PAPER), 25f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 0), ItemStack(ItemLightPlate, 1, 0), 120f)
    addHydraulicPressRecipe(ItemStack(GOLD_NUGGET, 6), ItemStack(ItemLightPlate, 1, 1), 50f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 2), ItemStack(ItemLightPlate, 1, 2), 100f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 3), ItemStack(ItemLightPlate, 1, 3), 50f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 4), ItemStack(ItemLightPlate, 1, 4), 120f)
    addHydraulicPressRecipe(ItemStack(ItemNugget, 6, 5), ItemStack(ItemLightPlate, 1, 5), 150f)
    addHydraulicPressRecipe(ItemStack(BlockWoodChip), ItemStack(BlockFiberboard, 4), 50f)

    //KILN RECIPES
    addKilnRecipe(ItemStack(COAL_BLOCK), BlockCoke.defaultState, 50, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
    addKilnRecipe(ItemStack(Blocks.LOG, 1, 0), BlockCharcoalSlab.defaultState, 25, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
    addKilnRecipe(ItemStack(Blocks.SAND), Blocks.GLASS.defaultState, 25, GLASS_MAKING_TEMP, QUARTZ_MELTING_POINT)
    addKilnRecipe(ItemStack(Blocks.CLAY), Blocks.HARDENED_CLAY.defaultState, 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)
    addKilnRecipe(ItemStack(BlockFluxedGravel), BlockGlazedBrick.defaultState, 25, FURNACE_BRICK_TEMP, QUARTZ_MELTING_POINT)
    addKilnRecipe(ItemStack(Blocks.SPONGE, 1, 1), ItemStack(Blocks.SPONGE, 1, 0), 25, WATER_BOILING_POINT, COKE_REACTION_TEMP)

    //KILN SHELF RECIPES
    addKilnRecipe(ItemStack(COAL, 1, 0), ItemStack(ItemCoke), 50, COKE_REACTION_TEMP, CARBON_SUBLIMATION_POINT)
    addKilnRecipe(ItemStack(CLAY_BALL), ItemStack(BRICK), 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)
    addKilnRecipe(ItemStack(CHORUS_FRUIT), ItemStack(CHORUS_FRUIT_POPPED, 1, 0), 25, DEFAULT_SMELTING_TEMPERATURE, QUARTZ_MELTING_POINT)

    //GRINDER RECIPES
    addGrinderRecipe(ItemStack(Blocks.IRON_ORE, 1, 0), ItemCrushedOre.stack(size = 1, meta = 0), ItemStack(Blocks.GRAVEL), 1f, 5f)
    //TODO: Remove iron placeholder values
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

    addStampedPartRecipe(ItemStack(Items.MINECART), "I#I", "III", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Items.CAULDRON), "I#I", "I#I", "III", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Items.BUCKET), "I#I", "#I#", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Items.IRON_DOOR, 3), "II", "II", "II", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE), "II", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Blocks.IRON_TRAPDOOR), "II", "II", 'I', "lightPlateIron")
    addStampedPartRecipe(ItemStack(Blocks.HOPPER), "I#I", "ICI", "#I#", 'I', "lightPlateIron", 'C', "chestWood")
    addStampedPartRecipe(ItemStack(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE), "II", 'I', "lightPlateGold")
    addStampedRecipe(ItemStack(Items.SHEARS), "#I", "I#", 'I', "lightPlateIron")
    addStampedRecipe(ItemStack(Items.IRON_SWORD), "I", "I", "S", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.IRON_AXE), "II", "IS", "#S", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.IRON_HOE), "II", "#S", "#S", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.IRON_SHOVEL), "I", "S", "S", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.IRON_PICKAXE), "III", "#S#", "#S#", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.IRON_BOOTS), "I#I", "I#I", 'I', "lightPlateIron")
    addStampedRecipe(ItemStack(Items.IRON_HELMET), "III", "I#I", 'I', "lightPlateIron")
    addStampedRecipe(ItemStack(Items.IRON_LEGGINGS), "III", "I#I", "I#I", 'I', "lightPlateIron")
    addStampedRecipe(ItemStack(Items.IRON_CHESTPLATE), "I#I", "III", "III", 'I', "lightPlateIron")
    addStampedRecipe(ItemStack(Items.SHIELD), "PIP", "PPP", "#P#", 'I', "lightPlateIron", 'P', "plankWood")
    addStampedRecipe(ItemStack(ItemIronHammer), "II#", "ISI", "#S#", 'I', "lightPlateIron", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_SWORD), "I", "I", "S", 'I', "lightPlateGold", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_AXE), "II", "IS", "#S", 'I', "lightPlateGold", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_HOE), "II", "#S", "#S", 'I', "lightPlateGold", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_SHOVEL), "I", "S", "S", 'I', "lightPlateGold", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_PICKAXE), "III", "#S#", "#S#", 'I', "lightPlateGold", 'S', "stickWood")
    addStampedRecipe(ItemStack(Items.GOLDEN_BOOTS), "I#I", "I#I", 'I', "lightPlateGold")
    addStampedRecipe(ItemStack(Items.GOLDEN_HELMET), "III", "I#I", 'I', "lightPlateGold")
    addStampedRecipe(ItemStack(Items.GOLDEN_LEGGINGS), "III", "I#I", "I#I", 'I', "lightPlateGold")
    addStampedRecipe(ItemStack(Items.GOLDEN_CHESTPLATE), "I#I", "III", "III", 'I', "lightPlateGold")
    addHeavyRecipe(ItemStack(Items.IRON_BOOTS), "I#I", "I#I", 'I', "heavyPlateIron")
    addHeavyRecipe(ItemStack(Items.IRON_HELMET), "III", "I#I", 'I', "heavyPlateIron")
    addHeavyRecipe(ItemStack(Items.IRON_LEGGINGS), "III", "I#I", "I#I", 'I', "heavyPlateIron")
    addHeavyRecipe(ItemStack(Items.IRON_CHESTPLATE), "I#I", "III", "III", 'I', "heavyPlateIron")
    addHeavyRecipe(ItemStack(Items.GOLDEN_BOOTS), "I#I", "I#I", 'I', "heavyPlateGold")
    addHeavyRecipe(ItemStack(Items.GOLDEN_HELMET), "III", "I#I", 'I', "heavyPlateGold")
    addHeavyRecipe(ItemStack(Items.GOLDEN_LEGGINGS), "III", "I#I", "I#I", 'I', "heavyPlateGold")
    addHeavyRecipe(ItemStack(Items.GOLDEN_CHESTPLATE), "I#I", "III", "III", 'I', "heavyPlateGold")
    addHeavyRecipe(ItemStack(Items.SHIELD), "PIP", "PPP", "#P#", 'I', "heavyPlateIron", 'P', "plankWood")

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
    addRecipe(ItemStack(ItemIronHammer), true, "PX#", "XZX", "#Z#", 'P', "lightPlateIron", 'X', "ingotIron", 'Z', "stickWood")
    addRecipe(ItemStack(ItemStoneHammer), true, "XX#", "XZX", "#Z#", 'X', "cobblestone", 'Z', "stickWood")

    addRecipe(ItemStack(BlockFluxedGravel, 4), "CSC", "LGF", "CSC", 'G', "gravel", 'C', Items.CLAY_BALL, 'L', "pebblesLead", 'F', "pebblesCobalt_Mgc", 'S', "sand")

    addRecipe(ItemStack(ItemPulpWood, 8), "III", "IXI", "III", 'I', ItemStack(ItemWoodChip), 'X', "listAllWater")
    addRecipe(ItemStack(BlockLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockLimestone))
    addRecipe(ItemStack(BlockTileLimestone, 4), "XY", "YX", 'X', ItemStack(BlockLimestone), 'Y', ItemStack(BlockBurntLimestone))
    addRecipe(ItemStack(BlockBurntLimestone, 4, 1), "XX", "XX", 'X', ItemStack(BlockBurntLimestone))
//    addRecipe(ItemStack(ItemGuideBook), "CB", 'C', "ingotCopper", 'B', of(BOOK))
    addRecipe(ItemStack(BlockIncendiaryGenerator), "ICI", "IFI", "IBI", 'I', "ingotIron", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))

    addRecipe(ItemStack(ItemCoilOfWire), "#C#", "C#C", "#C#", 'C', "ingotCopper")
    addRecipe(ItemStack(ItemMesh), "CCC", "CCC", "CCC", 'C', ItemStack(Blocks.IRON_BARS))
    addRecipe(ItemStack(ItemVoltmeter), "WIW", "PRP", "CPC", 'C', "ingotCopper", 'I', "ingotIron", 'P', "plankWood", 'R', ItemCoilOfWire, 'W', of(WOOL))
    addRecipe(ItemStack(ItemThermometer), "GIG", "PRP", "TPT", 'T', "ingotTungsten", 'I', "ingotIron", 'P', "plankWood", 'R', ItemCoilOfWire, 'G', "blockGlass")

    addRecipe(ItemStack(BlockElectricFurnace), "CCC", "CFC", "CBC", 'C', "ingotCopper", 'F', of(FURNACE), 'B', of(BRICK_BLOCK))
    addRecipe(ItemStack(BlockInfiniteWater), "IBI", "TCT", "IBI", 'C', "ingotCobalt", 'I', "ingotIron", 'T', "ingotTungsten", 'B', Items.WATER_BUCKET)

    addRecipe(ItemStack(BlockMachineBlock, 2), "SSS", "I#I", "PIP", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockMesh, 3), "SPS", "MMM", "SPS", 'M', ItemMesh, 'S', BlockBurntLimestone, 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockHydraulicPress), "PPP", "IMI", "SSS", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron", 'M', BlockMachineBlock)
    addRecipe(ItemStack(BlockSifter), "PHP", "IMI", "SSS", 'I', "ingotLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron", 'M', BlockMachineBlock, 'H', Blocks.HOPPER)
    addRecipe(ItemStack(BlockGrinder), "PPP", "IWI", "MMM", 'I', "lightPlateLead", 'M', BlockMachineBlock, 'P', "heavyPlateIron", 'W', ItemCoilOfWire)
    addRecipe(ItemStack(BlockMachineBlockSupportColumn, 4), "PSP", "OSO", "PSP", 'O', "lightPlateLead", 'S', BlockBurntLimestone, 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockStripedMachineBlock, 8), "YSB", "S#S", "BSY", 'Y', "dyeYellow", 'S', BlockBurntLimestone, 'B', "dyeBlack")

    addRecipe(ItemStack(BlockBattery), "ILI", "CPC", "LCL", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'L', "lightPlateLead", 'C', "ingotCobalt")
    addRecipe(ItemStack(BlockElectricPoleAdapter), "INI", "IPI", "ICI", 'I', "lightPlateIron", 'P', ItemCoilOfWire, 'N', BlockElectricConnector, 'C', "ingotCopper")
    addRecipe(ItemStack(BlockElectricConnector, 8), "#C#", "WLW", "SSS", 'C', "ingotCopper", 'W', of(WOOL), 'S', of(STONE_SLAB), 'L', "ingotLead")
    addRecipe(ItemStack(BlockAirLock), "TCT", "PSP", "TCT", 'C', "heavyPlateCobalt", 'T', "ingotTungsten", 'P', ItemCoilOfWire, 'S', of(SPONGE))

    addRecipe(ItemStack(BlockTableSieve), "SPS", "I#I", "WWW", 'W', "plankWood", 'I', "ingotIron", 'P', of(PAPER), 'S', "slabWood")
    addRecipe(ItemStack(BlockCrushingTable), "SSS", "WWW", "W#W", 'S', of(STONE_SLAB), 'W', "logWood")
    addRecipe(ItemStack(BlockKiln), "BDB", "BPB", 'B', BlockBurntLimestone, 'P', "lightPlateCopper", 'D', Items.IRON_DOOR)
    addRecipe(ItemStack(BlockKilnShelf), "BPB", "B#B", 'B', of(IRON_BARS), 'P', "lightPlateIron")
    addRecipe(ItemStack(BlockFeedingTrough), "M#M", "SWS", 'S', "stickWood", 'W', "plankWood", 'M', "slabWood")

    addRecipe(ItemStack(BlockElectricPole), "CPC", "#W#", "#W#", 'P', "plankWood", 'C', BlockElectricConnector, 'W', "logWood")
    addRecipe(ItemStack(BlockSolarPanel), "LCL", "I#I", "IPI", 'L', "lightPlateLead", 'C', ItemCoilOfWire, 'I', "lightPlateIron", 'P', "heavyPlateIron")
    addRecipe(ItemStack(BlockElectricalMachineBlock), "III", "PWP", "III",  'I', "ingotIron", 'P', "lightPlateIron", 'W', ItemCoilOfWire)

    addRecipe(ItemStack(BlockElectricHeater), "XPX", "XFX", "XFX", 'P', "lightPlateCopper", 'F', ItemCoilOfWire, 'X', "ingotBrick")
    addRecipe(ItemStack(BlockBrickFurnace), "XXX", "XFX", "XPX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', Blocks.FURNACE)
    addRecipe(ItemStack(BlockFirebox), "XPX", "XFX", "XXX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', Blocks.FURNACE)
    addRecipe(ItemStack(BlockIcebox), "XXX", "XFX", "XPX", 'X', "cobblestone", 'P', "lightPlateCopper", 'F', Items.CAULDRON)
    addRecipe(ItemStack(BlockHeatReservoir), "XPX", "XFX", "XXX", 'X', "ingotBrick", 'P', "lightPlateCopper", 'F', BlockCompactedCopper)
    addRecipe(ItemStack(BlockHeatSink), "PPP", "XPX", 'X', "ingotBrick", 'P', "lightPlateCopper")
    addRecipe(ItemStack(BlockHeatPipe, 8), "BBB", "PIP", "BBB", 'B', "ingotBrick", 'P', "lightPlateCopper", 'I', "ingotCopper")
    addRecipe(ItemStack(BlockRedstoneHeatPipe), "C", "P", 'P', BlockHeatPipe, 'C', Items.REPEATER)

    //why? "Found an itemStack with a null item. This is an error from another mod."
//    addRecipe(ItemStack(BlockThermometer), "#C#", "IPD", 'C', Items.COMPARATOR, 'I', "ingotTungsten", 'D', "ingotIron", 'P', BlockHeatPipe)

    addRecipe(ItemStack(BlockCoke), "XXX", "XXX", "XXX", 'X', of(ItemCoke))
    addRecipe(ItemStack(ItemCoke, 9), "###", "#X#", "###", 'X', of(BlockCoke))

    //ICEBOX RECIPES
    addIceboxRecipeWater(ItemStack(Items.SNOWBALL), 125, false)
    addIceboxRecipeWater(ItemStack(Blocks.SNOW), 500, false)
    addIceboxRecipeWater(ItemStack(Blocks.ICE), 900, true)
    addIceboxRecipeWater(ItemStack(Blocks.PACKED_ICE), 1000, false)

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
    for (i in ItemPebbles.variants.indices) {
        addTableSieveRecipe(ItemStack(ItemCrushedOre, 1, i), ItemStack(ItemPebbles, 1, i), ItemStack(COBBLESTONE), 0.15f)
    }
    addTableSieveRecipe(ItemStack(ItemCrushedLapis), ItemStack(ItemPebblesLapis), ItemStack(DYE, 1, 4), 0.1f)
    addTableSieveRecipe(ItemStack(ItemCrushedCoal), ItemStack(ItemPebblesCoal), ItemStack(DIAMOND), 0.001f)
    addTableSieveRecipe(ItemStack(Blocks.GRAVEL), ItemStack(Items.FLINT), ItemStack(Items.FLINT), 0.15f)

    //SIFTER RECIPES
    for (i in ItemPebbles.variants.indices) {
        addSifterRecipe(ItemStack(ItemCrushedOre, 1, i), ItemStack(ItemPebbles, 1, i), ItemStack(ItemPebbles, 1, ItemPebbles.secondaries[i]!!), 0.1f, ItemStack(GRAVEL), 0.15f, 20f)
    }
    addSifterRecipe(ItemStack(ItemCrushedLapis), ItemStack(ItemPebblesLapis), ItemStack(DYE, 1, 4), 0.1f, ItemStack(COBBLESTONE), 0.1f, 20f)
    addSifterRecipe(ItemStack(ItemCrushedCoal), ItemStack(ItemPebblesCoal), ItemStack(Items.COAL), 0.1f, ItemStack(DIAMOND), 0.001f, 20f)
    addSifterRecipe(ItemStack(Blocks.GRAVEL), ItemStack(Items.FLINT), ItemStack(Blocks.SAND), 0.1f, ItemStack(Items.FLINT), 0.2f, 20f)

    //@formatter:on
}

private fun addRecipe(result: ItemStack, vararg craft: Any) {
    GameRegistry.addRecipe(ShapedOreRecipe(result, *craft))
}

private fun addEnchantRecipe(result: ItemStack, enchants: List<Pair<Enchantment, Int>>, vararg craft: Any) {
    enchants.forEach {
        result.addEnchantment(it.first, it.second)
    }
    addRecipe(result, *craft)
}

private fun addLoreRecipe(result: ItemStack, lore: String, enchants: List<Pair<Enchantment, Int>>, vararg craft: Any) {
    result.setLore(listOf(lore))
    addEnchantRecipe(result, enchants, *craft)
}

private fun addEnchantRecipe(result: ItemStack, lore: String, enchants: List<Pair<Enchantment, Int>>,
                             vararg craft: Any) {
    addLoreRecipe(result, lore, enchants, *craft)
}

private fun addLoreRecipe(result: ItemStack, lore: String, vararg craft: Any) {
    result.setLore(listOf(lore))
    addRecipe(result, *craft)
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

private fun addHeatExchangerRecipe(input: FluidStack, output: FluidStack, heat: Long, minTemp: Double, maxTemp: Double,
                                   reverseLow: Boolean, reverseHigh: Boolean) {
    HeatExchangerRecipeManager.registerRecipe(
            HeatExchangerRecipeManager.createRecipe(input, output, heat, minTemp, maxTemp, reverseLow, reverseHigh))
}

private fun addTableSieveRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float) {
    TableSieveRecipeManager.registerRecipe(TableSieveRecipeManager.createRecipe(input, output0, output1, prob, true))
}

private fun addTableSieveRecipe(input: ItemStack, output: ItemStack) {
    TableSieveRecipeManager.registerRecipe(TableSieveRecipeManager.createRecipe(input, output, output, 0f, true))
}

private fun addSifterRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob1: Float, output2: ItemStack,
                            prob2: Float, duration: Float) {
    SifterRecipeManager.registerRecipe(
            SifterRecipeManager.createRecipe(input, output0, output1, prob1, output2, prob2, duration, true))
}

private fun addSifterRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob1: Float, duration: Float) {
    SifterRecipeManager.registerRecipe(
            SifterRecipeManager.createRecipe(input, output0, output1, prob1, output1, 0f, duration, true))
}

private fun addSifterRecipe(input: ItemStack, output0: ItemStack, duration: Float) {
    SifterRecipeManager.registerRecipe(
            SifterRecipeManager.createRecipe(input, output0, output0, 0f, output0, 0f, duration, true))
}

private fun addHydraulicPressRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
    HydraulicPressRecipeManager.registerRecipe(HydraulicPressRecipeManager.createRecipe(input, output, ticks, true))
}

private fun addKilnRecipe(input: ItemStack, output: ItemStack, duration: Int, minTemp: Double, maxTemp: Double) {
    KilnRecipeManager.registerRecipe(KilnRecipeManager.createRecipe(input, output, duration, minTemp, maxTemp, true))
}

private fun addKilnRecipe(input: ItemStack, output: IBlockState, duration: Int, minTemp: Double, maxTemp: Double) {
    KilnRecipeManager.registerRecipe(KilnRecipeManager.createRecipe(input, output, duration, minTemp, maxTemp, true))
}

private fun addIceboxRecipe(input: ItemStack, output: FluidStack, heat: Long, specificHeat: Double, minTemp: Double,
                            maxTemp: Double, reverse: Boolean) {
    IceboxRecipeManager.registerRecipe(
            IceboxRecipeManager.createRecipe(input, output, heat, specificHeat, minTemp, maxTemp, reverse))
}

private fun addIceboxRecipeWater(input: ItemStack, output: Int, reverse: Boolean) {
    IceboxRecipeManager.registerRecipe(IceboxRecipeManager.createRecipe(input, FluidStack(FluidRegistry.WATER, output),
            (WATER_HEAT_OF_FUSION * output / 1000).toLong(), WATER_HEAT_CAPACITY, WATER_MELTING_POINT,
            WATER_BOILING_POINT, reverse))
}

private fun addGrinderRecipe(input: ItemStack, output0: ItemStack, output1: ItemStack, prob: Float, ticks: Float) {
    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output0, output1, prob, ticks, true))
}

private fun addGrinderRecipe(input: ItemStack, output: ItemStack, ticks: Float) {
    GrinderRecipeManager.registerRecipe(GrinderRecipeManager.createRecipe(input, output, output, 0f, ticks, true))
}

private fun addStampedRecipe(result: ItemStack, vararg craft: Any) {
    addEnchantRecipe(result, "Stamped", listOf(Pair(Enchantments.UNBREAKING, 1)), *craft)
}

private fun addStampedPartRecipe(result: ItemStack, vararg craft: Any) {
    addLoreRecipe(result, "Stamped", *craft)
}

private fun addHeavyRecipe(result: ItemStack, vararg craft: Any) {
    addEnchantRecipe(result, "Heavy Duty", listOf(Pair(Enchantments.UNBREAKING, 3), Pair(Enchantments.PROTECTION, 1)),*craft)
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