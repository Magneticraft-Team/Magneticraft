package com.cout970.magneticraft.api.registries.generators.thermopile;

import net.minecraft.block.state.IBlockState;

/**
 * Created by cout970 on 2017/08/28.
 */
public interface IThermopileRecipe {

    /**
     * This is the blockstate used to index the recipe
     *
     * @return blockstate used to get the recipe
     */
    IBlockState getBlockState();


    /**
     * Current temperature
     *
     * @return Temperature of the block in kelvin
     */
    float getTemperature();

    /**
     * Conductivity of the block, this depends on the material and how OP is the recipe
     *
     * @return Conductivity of the block in Watts / (meter * kelvin)
     */
    float getConductivity();
}
