package com.cout970.magneticraft.systems.integration.jei

import com.cout970.magneticraft.MOD_NAME
import com.cout970.magneticraft.api.MagneticraftApi
import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe
import com.cout970.magneticraft.api.registries.machines.crushingtable.ICrushingTableRecipe
import com.cout970.magneticraft.api.registries.machines.gasificationunit.IGasificationUnitRecipe
import com.cout970.magneticraft.api.registries.machines.grinder.IGrinderRecipe
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.HydraulicPressMode
import com.cout970.magneticraft.api.registries.machines.hydraulicpress.IHydraulicPressRecipe
import com.cout970.magneticraft.api.registries.machines.oilheater.IOilHeaterRecipe
import com.cout970.magneticraft.api.registries.machines.refinery.IRefineryRecipe
import com.cout970.magneticraft.api.registries.machines.sifter.ISieveRecipe
import com.cout970.magneticraft.api.registries.machines.sluicebox.ISluiceBoxRecipe
import com.cout970.magneticraft.features.items.EnumMetal
import com.cout970.magneticraft.features.manual_machines.ContainerFabricator
import com.cout970.magneticraft.misc.*
import com.cout970.magneticraft.misc.gui.formatHeat
import com.cout970.magneticraft.misc.inventory.Inventory
import com.cout970.magneticraft.misc.inventory.isNotEmpty
import com.cout970.magneticraft.misc.inventory.set
import com.cout970.magneticraft.misc.inventory.stack
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.integration.crafttweaker.ifNonEmpty
import com.cout970.magneticraft.systems.tilemodules.ModuleCrushingTable
import mezz.jei.api.*
import mezz.jei.api.gui.IDrawable
import mezz.jei.api.gui.IRecipeLayout
import mezz.jei.api.ingredients.IIngredients
import mezz.jei.api.recipe.IRecipeCategory
import mezz.jei.api.recipe.IRecipeCategoryRegistration
import mezz.jei.api.recipe.IRecipeWrapper
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import mezz.jei.api.recipe.transfer.IRecipeTransferError
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler
import mezz.jei.gui.elements.DrawableResource
import mezz.jei.util.Translator
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.FontRenderer
import net.minecraft.client.gui.GuiScreen
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompressedStreamTools
import net.minecraft.nbt.NBTTagString
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import java.awt.Color
import java.io.ByteArrayOutputStream
import com.cout970.magneticraft.features.electric_machines.Blocks as ElectricBlocks
import com.cout970.magneticraft.features.heat_machines.Blocks as HeatBlocks
import com.cout970.magneticraft.features.manual_machines.Blocks as ManualBlocks
import com.cout970.magneticraft.features.multiblocks.Blocks as Multiblocks

/**
 * Created by cout970 on 23/07/2016.
 */
@JEIPlugin
class MagneticraftPlugin : IModPlugin {

    companion object {
        lateinit var INSTANCE: MagneticraftPlugin
        const val CRUSHING_TABLE_ID = "magneticraft.crushing_table"
        const val SLUICE_BOX_ID = "magneticraft.sluice_box"
        const val GRINDER_ID = "magneticraft.grinder"
        const val SIEVE_ID = "magneticraft.sieve"
        const val HYDRAULIC_PRESS_ID = "magneticraft.hydraulic_press"
        const val OIL_HEATER_ID = "magneticraft.oil_heater"
        const val REFINERY_ID = "magneticraft.refinery"
        const val GASIFICATION_UNIT_ID = "magneticraft.gasification_unit"
        const val THERMOPILE_ID = "magneticraft.thermopile"
    }

    init {
        INSTANCE = this
    }

    private lateinit var runtime: IJeiRuntime

    override fun onRuntimeAvailable(jeiRuntime: IJeiRuntime) {
        runtime = jeiRuntime
    }

    fun getSearchBar(): IIngredientFilter = runtime.ingredientFilter

    override fun register(registry: IModRegistry) {

        registry.recipeTransferRegistry.addRecipeTransferHandler(FabricatorHandler, VanillaRecipeCategoryUid.CRAFTING)

        registry.handleRecipes(ICrushingTableRecipe::class.java, ::CrushingTableRecipeWrapper, CRUSHING_TABLE_ID)
        registry.addRecipeCatalyst(ManualBlocks.crushingTable.stack(), CRUSHING_TABLE_ID)
        registry.addRecipes(MagneticraftApi.getCrushingTableRecipeManager().recipes, CRUSHING_TABLE_ID)

        registry.handleRecipes(ISluiceBoxRecipe::class.java, ::SluiceBoxRecipeWrapper, SLUICE_BOX_ID)
        registry.addRecipeCatalyst(ManualBlocks.sluiceBox.stack(), SLUICE_BOX_ID)
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

        registry.handleRecipes(IOilHeaterRecipe::class.java, ::OilHeaterRecipeWrapper, OIL_HEATER_ID)
        registry.addRecipeCatalyst(Multiblocks.oilHeater.stack(), OIL_HEATER_ID)
        registry.addRecipes(MagneticraftApi.getOilHeaterRecipeManager().recipes, OIL_HEATER_ID)

        registry.handleRecipes(IRefineryRecipe::class.java, ::RefineryRecipeWrapper, REFINERY_ID)
        registry.addRecipeCatalyst(Multiblocks.refinery.stack(), REFINERY_ID)
        registry.addRecipes(MagneticraftApi.getRefineryRecipeManager().recipes, REFINERY_ID)

        registry.handleRecipes(IGasificationUnitRecipe::class.java, ::GasificationUnitRecipeWrapper, GASIFICATION_UNIT_ID)
        registry.addRecipeCatalyst(HeatBlocks.gasificationUnit.stack(), GASIFICATION_UNIT_ID)
        registry.addRecipes(MagneticraftApi.getGasificationUnitRecipeManager().recipes, GASIFICATION_UNIT_ID)

        registry.handleRecipes(IThermopileRecipe::class.java, ::ThermopileRecipeWrapper, THERMOPILE_ID)
        registry.addRecipeCatalyst(ElectricBlocks.thermopile.stack(), THERMOPILE_ID)
        registry.addRecipes(MagneticraftApi.getThermopileRecipeManager().recipes.filter(::validThermopileRecipe), THERMOPILE_ID)

//        registry.addRecipeClickArea(...) fuck
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
                    addTooltip(t("text.magneticraft.jei.time", recipeWrapper.recipe.duration.toString()))
                })

                // mode indicator

                val modeItem = when (recipeWrapper.recipe.mode!!) {
                    HydraulicPressMode.LIGHT -> EnumMetal.COPPER.getIngot()
                    HydraulicPressMode.MEDIUM -> EnumMetal.COPPER.getLightPlate()
                    HydraulicPressMode.HEAVY -> EnumMetal.COPPER.getHeavyPlate()
                }

                modeItem.addTooltip(t("text.magneticraft.jei.hydraulic_hammer." +
                    recipeWrapper.recipe.mode.name.toLowerCase()))

                recipeLayout.itemStacks.init(2, false, 78, 25)
                recipeLayout.itemStacks.set(2, modeItem)
            }
        ))

        registry.addRecipeCategories(RecipeCategory<OilHeaterRecipeWrapper>(
            id = OIL_HEATER_ID,
            backgroundTexture = "oil_heater",
            unlocalizedTitle = "text.magneticraft.jei.oil_heater",
            initFunc = { recipeLayout, recipeWrapper, _ ->

                val cap1 = recipeWrapper.recipe.input.amount
                val cap2 = recipeWrapper.recipe.output.amount

                recipeLayout.fluidStacks.init(0, true, 49, 42, 16, 16, cap1, false, null)
                recipeLayout.fluidStacks.init(1, false, 49, 11, 16, 16, cap2, false, null)

                recipeLayout.fluidStacks.set(0, recipeWrapper.recipe.input)
                recipeLayout.fluidStacks.set(1, recipeWrapper.recipe.output)
            }
        ))

        registry.addRecipeCategories(RecipeCategory<RefineryRecipeWrapper>(
            id = REFINERY_ID,
            backgroundTexture = "refinery",
            unlocalizedTitle = "text.magneticraft.jei.refinery",
            initFunc = { recipeLayout, recipeWrapper, _ ->

                val cap1 = recipeWrapper.recipe.input.amount
                val cap2 = recipeWrapper.recipe.output0?.amount ?: 10
                val cap3 = recipeWrapper.recipe.output1?.amount ?: 10
                val cap4 = recipeWrapper.recipe.output2?.amount ?: 10

                recipeLayout.fluidStacks.init(0, true, 40, 48, 16, 16, cap1, false, null)
                recipeLayout.fluidStacks.init(1, false, 60, 48, 16, 16, cap2, false, null)
                recipeLayout.fluidStacks.init(2, false, 60, 30, 16, 16, cap3, false, null)
                recipeLayout.fluidStacks.init(3, false, 60, 12, 16, 16, cap4, false, null)

                recipeLayout.fluidStacks.set(0, recipeWrapper.recipe.input)
                recipeWrapper.recipe.output0?.let { recipeLayout.fluidStacks.set(1, it) }
                recipeWrapper.recipe.output1?.let { recipeLayout.fluidStacks.set(2, it) }
                recipeWrapper.recipe.output2?.let { recipeLayout.fluidStacks.set(3, it) }
            }
        ))


        registry.addRecipeCategories(RecipeCategory<GasificationUnitRecipeWrapper>(
            id = GASIFICATION_UNIT_ID,
            backgroundTexture = "gasification_unit",
            unlocalizedTitle = "text.magneticraft.jei.gasification_unit",
            initFunc = { recipeLayout, recipeWrapper, _ ->
                val cap = recipeWrapper.recipe.fluidOutput?.amount ?: 10

                recipeLayout.itemStacks.init(0, true, 48, 10)
                recipeLayout.itemStacks.init(1, false, 48, 46)
                recipeLayout.fluidStacks.init(2, true, 48 + 18 + 4, 46, 16, 16, cap, false, null)

                recipeLayout.itemStacks.set(0, recipeWrapper.recipe.input)
                recipeWrapper.recipe.itemOutput.ifNonEmpty { recipeLayout.itemStacks.set(1, it) }
                recipeWrapper.recipe.fluidOutput?.let { recipeLayout.fluidStacks.set(2, it) }
            }
        ))

        registry.addRecipeCategories(RecipeCategory<ThermopileRecipeWrapper>(
            id = THERMOPILE_ID,
            backgroundTexture = "thermopile",
            unlocalizedTitle = "text.magneticraft.jei.thermopile",
            initFunc = { recipeLayout, recipeWrapper, _ ->
                recipeLayout.itemStacks.init(0, true, 48, 10)
                recipeLayout.itemStacks.set(0, recipeWrapper.recipe.blockState.stack())
            }
        ))
    }
}

class RecipeCategory<T : IRecipeWrapper>(
    val id: String,
    backgroundTexture: String,
    val unlocalizedTitle: String,
    val initFunc: (IRecipeLayout, T, IIngredients) -> Unit
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

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        val level = ModuleCrushingTable.getCrushingLevel(recipe.input)

        val name = when (level) {
            -1 -> t("text.magneticraft.jei.mining_level.-1")
            0 -> t("text.magneticraft.jei.mining_level.0")
            1 -> t("text.magneticraft.jei.mining_level.1")
            2 -> t("text.magneticraft.jei.mining_level.2")
            3 -> t("text.magneticraft.jei.mining_level.3")
            4 -> t("text.magneticraft.jei.mining_level.4")
            else -> t("text.magneticraft.jei.mining_level.other", level.toString())
        }

        minecraft.fontRenderer.drawString(name, 16, 68, Color.gray.rgb)
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

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(t("text.magneticraft.jei.require_water"), 0, 68, Color.gray.rgb)
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

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            32, 68, Color.gray.rgb)
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

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            32, 68, Color.gray.rgb)
    }
}

class HydraulicPressRecipeWrapper(val recipe: IHydraulicPressRecipe) : IRecipeWrapper {

    companion object {
        val slot = DrawableResource(resource("textures/gui/jei/slot.png"), 0, 0,
            24, 24, 22, 4, 75, 4,
            24, 24
        )
    }

    override fun getIngredients(ingredients: IIngredients) {

        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        ingredients.setOutput(ItemStack::class.java, recipe.output)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        slot.draw(minecraft)
        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            32, 68, Color.gray.rgb)
    }
}

class OilHeaterRecipeWrapper(val recipe: IOilHeaterRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(FluidStack::class.java, recipe.input)
        ingredients.setOutput(FluidStack::class.java, recipe.output)
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(formatHeat(recipe.minTemperature().toDouble()),
            10, 68, Color.gray.rgb)

        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            58, 68, Color.gray.rgb)
    }
}

class RefineryRecipeWrapper(val recipe: IRefineryRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(FluidStack::class.java, recipe.input)
        ingredients.setOutputs(FluidStack::class.java, listOfNotNull(recipe.output0, recipe.output1, recipe.output2))
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            32, 68, Color.gray.rgb)
    }
}

class GasificationUnitRecipeWrapper(val recipe: IGasificationUnitRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        if (recipe.useOreDictionaryEquivalencies()) {
            ingredients.setInputs(ItemStack::class.java, listOf(getOreDictEquivalents(recipe.input)))
        } else {
            ingredients.setInput(ItemStack::class.java, recipe.input)
        }
        recipe.itemOutput.ifNonEmpty { ingredients.setOutput(ItemStack::class.java, it) }
        recipe.fluidOutput?.let { ingredients.setOutput(FluidStack::class.java, it) }
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.drawString(formatHeat(recipe.minTemperature().toDouble()),
            10, 68, Color.gray.rgb)

        minecraft.fontRenderer.drawString(
            t("text.magneticraft.jei.time", recipe.duration.toString()),
            58, 68, Color.gray.rgb)
    }
}

class ThermopileRecipeWrapper(val recipe: IThermopileRecipe) : IRecipeWrapper {

    override fun getIngredients(ingredients: IIngredients) {
        ingredients.setInput(ItemStack::class.java, recipe.blockState.stack())
    }

    override fun drawInfo(minecraft: Minecraft, recipeWidth: Int, recipeHeight: Int, mouseX: Int, mouseY: Int) {
        minecraft.fontRenderer.text(
            t("text.magneticraft.jei.temperature", formatHeat(recipe.temperature.toDouble())),
            0, 35, Color.white.rgb
        )

        minecraft.fontRenderer.text(
            t("text.magneticraft.jei.head_conductivity", recipe.conductivity),
            0, 47, Color.white.rgb
        )
    }
}

private fun validThermopileRecipe(recipe: IThermopileRecipe): Boolean {
    return recipe.blockState.stack().isNotEmpty
}

private fun FontRenderer.text(txt: String, x: Int, y: Int, color: Int) {
    val width = getStringWidth(txt)
    val pad = 1

    GuiScreen.drawRect(
        x - pad,
        y - pad,
        x + width + pad,
        y + FONT_HEIGHT + pad,
        0x3F000000 or color.inv()
    )
    drawStringWithShadow(txt, x.toFloat(), y.toFloat(), color)
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

private object FabricatorHandler : IRecipeTransferHandler<ContainerFabricator> {
    override fun getContainerClass(): Class<ContainerFabricator> = ContainerFabricator::class.java

    override fun transferRecipe(container: ContainerFabricator, recipeLayout: IRecipeLayout, player: EntityPlayer, maxTransfer: Boolean, doTransfer: Boolean): IRecipeTransferError? {
        if (!doTransfer) return null
        val inv = Inventory(9)
        val inputIngredients = recipeLayout.itemStacks.guiIngredients.toList()
            .filter { it.second.isInput }
            .map { it.second }

        for (slot in 0..8) {
            val ingredient = inputIngredients.getOrNull(slot) ?: continue
            val stack = ingredient.allIngredients.firstOrNull() ?: continue
            if (stack.isEmpty) continue
            inv[slot] = stack.copy()
        }

        val ibd = IBD().also {
            val output = ByteArrayOutputStream()
            CompressedStreamTools.writeCompressed(inv.serializeNBT(), output)
            it.setByteArray(1, output.toByteArray())
        }

        container.sendUpdate(ibd)

        return null
    }
}