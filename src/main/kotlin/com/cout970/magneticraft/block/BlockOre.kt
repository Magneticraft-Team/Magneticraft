package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.BlockOreStates
import com.cout970.magneticraft.block.states.BlockProperties
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockOre : BlockBase(Material.ROCK, "ore_block") {

    init {
        soundType = SoundType.STONE
    }

    override fun getMaxModels():Int = 4

    override fun getModelLoc(i :Int): ModelResourceLocation {
        return ModelResourceLocation(registryName, "type="+getVariant(i))
    }

    override fun damageDropped(state: IBlockState): Int {
        return state.getValue(BlockProperties.blockOreState).ordinal
    }

    override fun getUnlocalizedName(stack: ItemStack?): String? {
        return unlocalizedName +"."+getVariant(stack?.metadata ?: 0)
    }

    private fun getVariant(metadata: Int): String = listOf("copper", "lead", "cobalt", "tungsten")[metadata]

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        for (i in 0..3)
            list?.add(ItemStack(itemIn, 1, i))
    }

    override fun createBlockState(): BlockStateContainer? {
        return BlockStateContainer(this, BlockProperties.blockOreState)
    }

    override fun getStateFromMeta(meta: Int): IBlockState? {
        return blockState.baseState.withProperty(BlockProperties.blockOreState, BlockOreStates.values()[meta])
    }

    override fun getMetaFromState(state: IBlockState?): Int {
        return state?.getValue(BlockProperties.blockOreState)?.ordinal ?: 0
    }
}