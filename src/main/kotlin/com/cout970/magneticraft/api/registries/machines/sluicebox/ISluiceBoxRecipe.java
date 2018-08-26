package com.cout970.magneticraft.api.registries.machines.sluicebox;

import java.util.List;
import kotlin.Pair;
import net.minecraft.item.ItemStack;

/**
 * Created by cout970 on 24/08/2016.
 */
public interface ISluiceBoxRecipe {

    /**
     * Whether or not this recipe use the OreDictionary to check if the input is valid
     *
     * @return if the OreDictionary will be used to compare items
     */
    boolean useOreDictionaryEquivalencies();

    /**
     * The input stack of this recipe The stacksize is ignored Note: this will return a COPY of the input not the
     * original instance of the input
     *
     * @return the stack used to find this recipe
     */
    ItemStack getInput();

    /**
     * The outputs of this recipe Note: this will return a COPY of the output not the original instance of the output
     *
     * @return The secondary outputs of this recipe
     */
    List<Pair<ItemStack, Float>> getOutputs();

    /**
     * Checks if this recipes has the same input as the given argument
     *
     * @param input the item to test if this recipe can be made from it
     *
     * @return true if the item matches the input of this recipe, false otherwise
     */
    boolean matches(ItemStack input);
}
