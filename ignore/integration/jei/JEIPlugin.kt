package com.cout970.magneticraft.integration.jei

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.block.BlockCrushingTable
import com.cout970.magneticraft.block.BlockTableSieve
import com.cout970.magneticraft.block.multiblock.BlockGrinder
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.block.multiblock.BlockKiln
import com.cout970.magneticraft.block.multiblock.BlockSifter
import com.cout970.magneticraft.integration.jei.crushingtable.CrushingTableRecipeCategory
import com.cout970.magneticraft.integration.jei.crushingtable.CrushingTableRecipeHandler
import com.cout970.magneticraft.integration.jei.grinder.GrinderRecipeCategory
import com.cout970.magneticraft.integration.jei.grinder.GrinderRecipeHandler
import com.cout970.magneticraft.integration.jei.hydraulicpress.HydraulicPressRecipeCategory
import com.cout970.magneticraft.integration.jei.hydraulicpress.HydraulicPressRecipeHandler
import com.cout970.magneticraft.integration.jei.kiln.KilnRecipeCategory
import com.cout970.magneticraft.integration.jei.kiln.KilnRecipeHandler
import com.cout970.magneticraft.integration.jei.sifter.SifterRecipeCategory
import com.cout970.magneticraft.integration.jei.sifter.SifterRecipeHandler
import com.cout970.magneticraft.integration.jei.sievetable.TableSieveRecipeCategory
import com.cout970.magneticraft.integration.jei.sievetable.TableSieveRecipeHandler
import mezz.jei.api.*
import mezz.jei.api.JEIPlugin
import mezz.jei.api.ingredients.IModIngredientRegistration
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/07/2016.
 */
@JEIPlugin
class JEIPlugin : IModPlugin {

    companion object {
        val CRUSHING_TABLE_ID = "magneticraft.crushing_table"
        val TABLE_SIEVE_ID = "magneticraft.table_sieve"
        val HYDRAULIC_PRESS_ID = "magneticraft.hydraulic_press"
        val KILN_ID = "magneticraft.kiln"
        val GRINDER_ID = "magneticraft.grinder"
        val SIFTER_ID = "magneticraft.softer"
    }

    override fun register(registry: IModRegistry) {

        //crushing table recipes
        registry.addRecipeHandlers(CrushingTableRecipeHandler)
        registry.addRecipeCategories(CrushingTableRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockCrushingTable), CRUSHING_TABLE_ID)
        registry.addRecipes(MagneticraftApi.getCrushingTableRecipeManager().recipes)

        //table sieve recipes
        registry.addRecipeHandlers(TableSieveRecipeHandler)
        registry.addRecipeCategories(TableSieveRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockTableSieve), TABLE_SIEVE_ID)
        registry.addRecipes(MagneticraftApi.getTableSieveRecipeManager().recipes)

        //hydraulic press recipes
        registry.addRecipeHandlers(HydraulicPressRecipeHandler)
        registry.addRecipeCategories(HydraulicPressRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockHydraulicPress), HYDRAULIC_PRESS_ID)
        registry.addRecipes(MagneticraftApi.getHydraulicPressRecipeManager().recipes)

        //grinder recipes
        registry.addRecipeHandlers(GrinderRecipeHandler)
        registry.addRecipeCategories(GrinderRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockGrinder), GRINDER_ID)
        registry.addRecipes(MagneticraftApi.getGrinderRecipeManager().recipes)

        //kiln recipes
        registry.addRecipeHandlers(KilnRecipeHandler)
        registry.addRecipeCategories(KilnRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockKiln), KILN_ID)
        registry.addRecipes(MagneticraftApi.getKilnRecipeManager().recipes)

        //sifter recipes
        registry.addRecipeHandlers(SifterRecipeHandler)
        registry.addRecipeCategories(SifterRecipeCategory)
        registry.addRecipeCategoryCraftingItem(ItemStack(BlockSifter), SIFTER_ID)
        registry.addRecipes(MagneticraftApi.getSifterRecipeManager().recipes)
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {}

    override fun registerItemSubtypes(subtypeRegistry: ISubtypeRegistry?) {}

    override fun registerIngredients(registry: IModIngredientRegistration) {}
}