package com.cout970.magneticraft.api.internal.registries.generators.thermopile

import com.cout970.magneticraft.api.registries.generators.thermopile.IThermopileRecipe
import net.minecraft.block.BlockState

/**
 * Created by cout970 on 2017/08/28.
 */
data class ThermopileRecipe(
    private val blockState: BlockState,
    private val temperature: Float,
    private val conductivity: Float
) : IThermopileRecipe {

    override fun getBlockState(): BlockState = blockState

    override fun getTemperature(): Float = temperature

    override fun getConductivity(): Float = conductivity
}