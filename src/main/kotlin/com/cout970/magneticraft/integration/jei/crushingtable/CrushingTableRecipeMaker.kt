package com.cout970.magneticraft.integration.jei.crushingtable

import com.cout970.magneticraft.api.registries.machines.crushingtable.CrushingTableRegistry
import java.util.*

/**
 * Created by cout970 on 23/07/2016.
 */
object CrushingTableRecipeMaker {

    fun getCrushingRecipes() : List<CrushingTableRecipeWrapper>{
        val list = LinkedList<CrushingTableRecipeWrapper>()
        for((input, output) in CrushingTableRegistry.getRecipes()){
            list.add(CrushingTableRecipeWrapper(input, output))
        }
        return list
    }
}