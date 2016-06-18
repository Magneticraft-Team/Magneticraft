package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.BlockOreStates
import com.cout970.magneticraft.block.states.BlockProperties
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockOre : BlockMultiState(Material.ROCK, "ore_block") {

    init {
        soundType = SoundType.STONE
    }

    override fun getMaxModels():Int = 4

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return unlocalizedName +"."+deserializeState(stack?.metadata ?: 0)
                .getValue(BlockProperties.blockOreState).getName()
    }

    override fun shouldItemBeDisplayed(itemIn: Item, tab: CreativeTabs, i: Int): Boolean = i < 4

    override fun getProperties(): Array<IProperty<*>> = arrayOf(BlockProperties.blockOreState)

    override fun deserializeState(meta: Int): IBlockState {
        return blockState.baseState.withProperty(BlockProperties.blockOreState, BlockOreStates.values()[meta])
    }

    override fun serializeState(state: IBlockState): Int {
        return state.getValue(BlockProperties.blockOreState).ordinal
    }
}