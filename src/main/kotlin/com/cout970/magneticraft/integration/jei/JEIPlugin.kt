package com.cout970.magneticraft.integration.jei

import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.block.BlockCrushingTable
import com.cout970.magneticraft.block.BlockTableSieve
import com.cout970.magneticraft.block.multiblock.BlockHydraulicPress
import com.cout970.magneticraft.integration.jei.crushingtable.CrushingTableRecipeCategory
import com.cout970.magneticraft.integration.jei.crushingtable.CrushingTableRecipeHandler
import com.cout970.magneticraft.integration.jei.hydraulicpress.HydraulicPressRecipeCategory
import com.cout970.magneticraft.integration.jei.hydraulicpress.HydraulicPressRecipeHandler
import com.cout970.magneticraft.integration.jei.sievetable.TableSieveRecipeCategory
import com.cout970.magneticraft.integration.jei.sievetable.TableSieveRecipeHandler
import mezz.jei.api.IJeiRuntime
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 23/07/2016.
 */
@JEIPlugin
class JEIPlugin : IModPlugin {

    companion object{
        val CRUSHING_TABLE_ID = "magneticraft.crushing_table"
        val TABLE_SIEVE_ID = "magneticraft.table_sieve"
        val HYDRAULIC_PRESS_ID = "magneticraft.hydraulic_press"
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
    }

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {}
}