package com.cout970.magneticraft.api.internal.registries.generators.thermopile

import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe
import net.minecraft.block.Block
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/08/28.
 */
class ThermopileRecipeWithDecay(
        val block: Block,
        val heat: Int,
        val replacement: IBlockState,
        val tempLimit: Int,
        val probability: Float
) : IThermopileRecipe {

    override fun getRecipeBlock(): Block = block

    override fun getHeat(world: World?, pos: BlockPos?, state: IBlockState?): Int = heat

    override fun applyDecay(world: World, pos: BlockPos, state: IBlockState, heatSource: Int, heatDrain: Int) {
        if (tempLimit in 1..heatSource || (tempLimit < 0 && heatDrain >= -tempLimit)) {
            if(world.rand.nextFloat() < probability){
                world.setBlockState(pos, replacement)
            }
        }
    }
}