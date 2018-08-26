package com.cout970.magneticraft.api.registries.machines.sluicebox;

import java.util.List;
import kotlin.Deprecated;
import kotlin.DeprecationLevel;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ISluiceBoxRecipeManager {

    /**
     * Retrieves the first recipe that matches the given input
     *
     * @param input the input to check the recipes
     *
     * @return the recipes that matches the input or null if none matches the input
     */
    @Nullable
    ISluiceBoxRecipe findRecipe(ItemStack input);

    /**
     * The list with all registered recipes
     */
    List<ISluiceBoxRecipe> getRecipes();

    /**
     * Register a recipe if is not already registered
     *
     * @param recipe The recipe to register
     *
     * @return if the registration has ended successfully
     */
    boolean registerRecipe(ISluiceBoxRecipe recipe);

    /**
     * Removes a recipe from the registry
     *
     * @param recipe The recipe to remove
     *
     * @return true if the recipe has been removed, false if the recipe was not registered
     */
    boolean removeRecipe(ISluiceBoxRecipe recipe);

    /**
     * Creates a default recipe
     *
     * @param input the input stack
     * @param primaryOutput the output stack that is always returned by the recipe
     * @param secondaryOutput the output stacks and probability of every item to be created by the sluice box
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     *
     * @deprecated Use any of the versions bellow
     */
    @Deprecated(message = "primaryOutput now can have a percentage too", level = DeprecationLevel.HIDDEN)
    ISluiceBoxRecipe createRecipe(ItemStack input, ItemStack primaryOutput,
                                  List<Pair<ItemStack, Float>> secondaryOutput, boolean oreDict);

    /**
     * Creates a default recipe
     * <p>
     * If you are using java, use the version bellow
     *
     * @param input the input stack
     * @param outputs the output stacks and probability of every item to be created by the sluice box
     * @param oreDict if ore dictionary should be used to check the inputs
     *
     * @return the new recipe
     */
    ISluiceBoxRecipe createRecipe(ItemStack input, List<Pair<ItemStack, Float>> outputs, boolean oreDict);

    /**
     * Same as the previous function but without having to use kotlin.Pair
     */
    default ISluiceBoxRecipe createRecipe(ItemStack input, List<ItemStack> outputItems,
                                          List<Float> outputPercents, boolean oreDict) {
        return createRecipe(input, CollectionsKt.zip(outputItems, outputPercents), oreDict);
    }
}
