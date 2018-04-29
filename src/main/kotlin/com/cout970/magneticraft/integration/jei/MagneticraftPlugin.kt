package com.cout970.magneticraft.integration.jei

import com.cout970.magneticraft.MOD_NAME
import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.block.ManualMachines
import com.cout970.magneticraft.block.Multiblocks
import com.cout970.magneticraft.item.EnumMetal
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.list
import com.cout970.magneticraft.util.newNbt
import com.cout970.magneticraft.util.resource
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.gui.elements.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.client.Minecraft
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.oredict.OreDictionary

/**
 * Created by cout970 on 23/07/2016.
 */
@JEIPlugin
class MagneticraftPlugin : IModPlugin {

    companion object {
        val CRUSHING_TABLE_ID = "magneticraft.crushing_table"
        val SLUICE_BOX_ID = "magneticraft.sluice_box"
        val GRINDER_ID = "magneticraft.grinder"
        val SIEVE_ID = "magneticraft.sieve"
        val HYDRAULIC_PRESS_ID = "magneticraft.hydraulic_press"
    }

    override fun register(registry: IModRegistry) {

        registry.handleRecipes(ICrushingTableRecipe::class.java, ::CrushingTableRecipeWrapper, CRUSHING_TABLE_ID)
        registry.addRecipeCatalyst(ManualMachines.crushingTable.stack(), CRUSHING_TABLE_ID)
        registry.addRecipes(MagneticraftApi.getCrushingTableRecipeManager().recipes, CRUSHING_TABLE_ID)

        registry.handleRecipes(ISluiceBoxRecipe::class.java, ::SluiceBoxRecipeWrapper, SLUICE_BOX_ID)
        registry.addRecipeCatalyst(ManualMachines.sluiceBox.stack(), SLUICE_BOX_ID)
        registry.addRecipes(MagneticraftApi.getSluiceBoxRecipeManager().recipes, SLUICE_BOX_ID)

        registry.handleRecipes(IGrinderRecipe::class.java, ::GrinderRecipeWrapper, GRINDER_ID)
        registry.addRecipeCatalyst(Multiblocks.grinder.stack(), GRINDER_ID)
        registry.addRecipes(MagneticraftApi.getGrinderRecipeManager().recipes, GRINDER_ID)

        registry.handleRecipes(ISieveRecipe::class.java, ::SieveRecipeWrapper, SIEVE_ID)
        registry.addRecipeCatalyst(Multiblocks.sieve.stack(), SIEVE_ID)
        registry.addRecipes(MagneticraftApi.getSieveRecipeManager().recipes, SIEVE_ID)

        registry.handleRecipes(IHydraulicPressRecipe::class.java, ::HydraulicPressRecipeWrapper, HYDRAULIC_PRESS_ID)
        registry.addRecipeCatalyst(Multiblocks.hydraulicPress.stack(), HYDRAULIC_PRESS_ID)
        registry.addRecipes(MagneticraftApi.getHydraulicPressRecipeManager().recipes, HYDRAULIC_PRESS_ID)

    }

    override fun registerCategories(registry: IRecipeCategoryRegistration) {

        registry.addRecipeCategories(RecipeCategory<CrushingTableRecipeWrapper>(
                id = CRUSHING_TABLE_ID,
                backgroundTexture = "crushing_table",
                unlocalizedTitle = "text.magneticraft.jei.crushing_table",
                initFunc = { recipeLayout, recipeWrapper, ing ->
                    recipeLayout.itemStacks.init(0, true, 48, 15 - 5)
                    recipeLayout.itemStacks.init(1, false, 48, 51 - 5)
                    recipeLayout.itemStacks.set(0, ing.getInputs(ItemStack::class.java)[0])
                    recipeLayout.itemStacks.set(1, recipeWrapper.recipe.output)
                }
        ))

        registry.addRecipeCategories(RecipeCategory<SluiceBoxRecipeWrapper>(
                id = SLUICE_BOX_ID,
                backgroundTexture = "sluice_box",
                unlocalizedTitle = "text.magneticraft.jei.sluice_box",
                initFunc = { recipeLayout, recipeWrapper, ing ->
                    val recipe = recipeWrapper.recipe

                    val outputs = recipe.outputs.filter { it.first.isNotEmpty }

                    recipeLayout.itemStacks.init(0, true, 41, 12)
                    val columns = Math.min(outputs.size, 9)
                    repeat(outputs.size) { index ->
                        val x = 48 + 18 * (index % 9) - 18 * Math.round(columns / 2.0 - 1).toInt()
                        val y = 51 + 18 * (index / 9) - 5
                        recipeLayout.itemStacks.init(index + 1, false, x, y)
                    }

                    recipeLayout.itemStacks.set(0, ing.getInputs(ItemStack::class.java)[0])
                    repeat(outputs.size) { index ->
                        val stack = outputs[index].first
                        val percent = outputs[index].second * 100f

                        stack.applyNonEmpty { addTooltip("%.2f%%".format(percent)) }

                        recipeLayout.itemStacks.set(index + 1, stack)
                    }
                }
        ))

        registry.addRecipeCategories(RecipeCategory<GrinderRecipeWrapper>(
                id = GRINDER_ID,
                backgroundTexture = "grinder",
                unlocalizedTitle = "text.magneticraft.jei.grinder",
                initFunc = { recipeLayout, recipeWrapper, _ ->
                    recipeLayout.itemStacks.init(0, true, 48, 10)
                    recipeLayout.itemStacks.init(1, false, 39, 46)
                    recipeLayout.itemStacks.init(2, false, 57, 46)
                    recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
                    recipeLayout.itemStacks.set(1, recipeWrapper.recipe.primaryOutput)
                    recipeLayout.itemStacks.set(2, recipeWrapper.recipe.secondaryOutput.applyNonEmpty {
                        addTooltip("%.2f%%".format(recipeWrapper.recipe.probability * 100))
                    })
                }
        ))

        registry.addRecipeCategories(RecipeCategory<SieveRecipeWrapper>(
                id = SIEVE_ID,
                backgroundTexture = "sieve",
                unlocalizedTitle = "text.magneticraft.jei.sieve",
                initFunc = { recipeLayout, recipeWrapper, _ ->
                    recipeLayout.itemStacks.init(0, true, 48, 10)
                    recipeLayout.itemStacks.init(1, false, 30, 46)
                    recipeLayout.itemStacks.init(2, false, 48, 46)
                    recipeLayout.itemStacks.init(3, false, 66, 46)

                    recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)

                    if (recipeWrapper.recipe.primaryChance > 0) {
                        recipeLayout.itemStacks.set(1, recipeWrapper.recipe.primary.applyNonEmpty {
                            addTooltip("%.2f%%".format(recipeWrapper.recipe.primaryChance * 100))
                        })
                    }
                    if (recipeWrapper.recipe.secondaryChance > 0) {
                        recipeLayout.itemStacks.set(2, recipeWrapper.recipe.secondary.applyNonEmpty {
                            addTooltip("%.2f%%".format(recipeWrapper.recipe.secondaryChance * 100))
                        })
                    }
                    if (recipeWrapper.recipe.tertiaryChance > 0) {
                        recipeLayout.itemStacks.set(3, recipeWrapper.recipe.tertiary.applyNonEmpty {
                            addTooltip("%.2f%%".format(recipeWrapper.recipe.tertiaryChance * 100))
                        })
                    }
                }
        ))

        registry.addRecipeCategories(RecipeCategory<HydraulicPressRecipeWrapper>(
                id = HYDRAULIC_PRESS_ID,
                backgroundTexture = "hydraulic_press",
                unlocalizedTitle = "text.magneticraft.jei.hydraulic_press",
                initFunc = { recipeLayout, recipeWrapper, _ ->
                    recipeLayout.itemStacks.init(0, true, 49, 11)
                    recipeLayout.itemStacks.init(1, false, 49, 42)

                    recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
                    recipeLayout.itemStacks.set(1, recipeWrapper.recipe.output.applyNonEmpty {
                        addTooltip("${recipeWrapper.recipe.duration} ticks")
                    })

                    // mode indicator

                    val modeItem = when (recipeWrapper.recipe.mode!!) {
                        HydraulicPressMode.LIGHT -> EnumMetal.COPPER.getIngot()
                        HydraulicPressMode.MEDIUM -> EnumMetal.COPPER.getLightPlate()
                        HydraulicPressMode.HEAVY -> EnumMetal.COPPER.getHeavyPlate()
                    }.apply { addTooltip("${recipeWrapper.recipe.mode.name.toLowerCase().capitalize()} mode") }

                    recipeLayout.itemStacks.init(2, false, 78, 25)
                    recipeLayout.itemStacks.set(2, modeItem)
                },
                extras = DrawableResource(resource("textures/gui/jei/slot.png"), 0, 0,
                        24, 24, 22, 4, 75, 4,
                        24, 24
                ).let { res -> { it: Minecraft -> res.draw(it) } } // create the resource only when the gui opens
        ))
    }
}

class RecipeCategory<T : IRecipeWrapper>(
        val id: String,
        backgroundTexture: String,
        val unlocalizedTitle: String,
        val initFunc: (IRecipeLayout, T, IIngredients) -> Unit,
        val extras: ((Minecraft) -> Unit)? = null
) : IRecipeCategory<T> {

    private val background = DrawableResource(
            resource("textures/gui/jei/$backgroundTexture.png"),
            0, 0, 64, 64, 5, 5, 25, 25, 64, 64)


    override fun getUid(): String = id
    override fun getBackground(): IDrawable = background
    override fun getTitle(): String = Translator.translateToLocal(unlocalizedTitle)
    override fun getModName(): String = MOD_NAME

    override fun setRecipe(recipeLayout: IRecipeLayout, recipeWrapper: T, ingredients: IIngredients) {
        initFunc(recipeLayout, recipeWrapper, ingredients)
    }

    override fun drawExtras(minecraft: Minecraft) {
        extras?.invoke(minecraft)
    }
}

class CrushingTableRecipeWrapper(val recipe: ICrushingTableRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {

        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }
}

class SluiceBoxRecipeWrapper(val recipe: ISluiceBoxRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutputs(ItemStack::class.java, recipe.outputs.map { it.first })
    }
}

class GrinderRecipeWrapper(val recipe: IGrinderRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {

        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutputs(ItemStack::class.java, listOf(recipe.primaryOutput, recipe.secondaryOutput))
    }
}

class SieveRecipeWrapper(val recipe: ISieveRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {

        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutputs(ItemStack::class.java, listOf(recipe.primary, recipe.secondary, recipe.tertiary))
    }
}

class HydraulicPressRecipeWrapper(val recipe: IHydraulicPressRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {

        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }
}

private inline fun ItemStack.applyNonEmpty(func: ItemStack.() -> Unit): ItemStack {
    if (this.isNotEmpty) func()
    return this
}

private fun ItemStack.addTooltip(str: String) {
    tagCompound = newNbt {
        add("display", newNbt {
            list("Lore") {
                appendTag(NBTTagString(str))
            }
        })
    }
}

private fun getOreDictEquivalents(stack: ItemStack): List<ItemStack> {
    if (stack.isEmpty) return emptyList()
    val ids = OreDictionary.getOreIDs(stack)
    if (ids.isEmpty()) return listOf(stack)
    val ores = ids.flatMap { id ->
        OreDictionary.getOres(OreDictionary.getOreName(id)).toList()
    }
    if (ores.isEmpty()) return listOf(stack)
    return ores
}