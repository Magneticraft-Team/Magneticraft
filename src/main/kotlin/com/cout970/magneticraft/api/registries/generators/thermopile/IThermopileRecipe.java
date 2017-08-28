package com.cout970.magneticraft.api.registries.generators.thermopile;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * Created by cout970 on 2017/08/28.
 */
public interface IThermopileRecipe {

    /**
     * This is the block used to index the recipe
     *
     * @return block used to get the recipe object
     */
    Block getRecipeBlock();

    /**
     * This is called every 20ticks (this may change) to retrieve the heat of the surrounding blocks,
     * if the temperature is higher than 0, this is a heatSource, otherwise this is a heatDrain
     *
     * @param world dimension of the block
     * @param pos   position of the block
     * @param state current state of the block
     * @return the temperature captured by the thermopile
     */
    int getHeat(World world, BlockPos pos, IBlockState state);

    /**
     * This method can be used to add block decay, for example, melting snow or making obsidian from lava
     *
     * @param world      dimension of the block
     * @param pos        position of the block
     * @param state      current state of the block
     * @param heatSource sum of all heat sources around the thermopile
     * @param heatDrain  sum of all heat drains around the thermopile
     */
    void applyDecay(World world, BlockPos pos, IBlockState state, int heatSource, int heatDrain);
}
