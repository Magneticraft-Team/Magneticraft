package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.BlockLimestoneStates
import com.cout970.magneticraft.block.states.BlockProperties
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object BlockBurnLimestone : BlockMultiState(Material.ROCK, "burn_limestone") {

    val STATE_MAP = mapOf(
            0 to defaultState.withProperty(BlockProperties.blockBurnLimestoneState, BlockLimestoneStates.values()[0]),
            1 to defaultState.withProperty(BlockProperties.blockBurnLimestoneState, BlockLimestoneStates.values()[1]),
            2 to defaultState.withProperty(BlockProperties.blockBurnLimestoneState, BlockLimestoneStates.values()[2])
    )

    val MODEL_MAP = mapOf(
            0 to ModelResourceLocation(registryName, getStateName(STATE_MAP[0]!!)),
            1 to ModelResourceLocation(registryName, getStateName(STATE_MAP[1]!!)),
            2 to ModelResourceLocation(registryName, getStateName(STATE_MAP[2]!!))
    )

    override fun getModels(): Map<Int, ModelResourceLocation> {
        return MODEL_MAP
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return unlocalizedName +"."+ getStateFromMeta(stack?.metadata ?: 0)?.getValue(BlockProperties.blockBurnLimestoneState)?.getName()
    }

    override fun getProperties(): Array<IProperty<*>> = arrayOf(BlockProperties.blockBurnLimestoneState)

    override fun isHiddenState(state: IBlockState, meta: Int): Boolean = false

    override fun getStateMap(): Map<Int, IBlockState> = STATE_MAP
}