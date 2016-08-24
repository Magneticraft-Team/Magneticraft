package com.cout970.magneticraft.block

import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object BlockBurntLimestone : BlockBase(Material.ROCK, "burnt_limestone") {

    lateinit var LIMESTONE_STATES: PropertyEnum<BlockLimestone.LimestoneStates>
        private set

    override val inventoryVariants = BlockLimestone.LimestoneStates.values().associate {
        it.ordinal to "type=${it.name}"
    }

    override fun damageDropped(state: IBlockState) = state.getValue(LIMESTONE_STATES).ordinal

    override fun getItemName(stack: ItemStack?) =
        "${super.getItemName(stack)}_${BlockLimestone.LimestoneStates.values()[stack?.metadata ?: 0].name.toLowerCase()}"

    override fun createBlockState(): BlockStateContainer {
        LIMESTONE_STATES = PropertyEnum.create("type", BlockLimestone.LimestoneStates::class.java)!!
        return BlockStateContainer(this, LIMESTONE_STATES)
    }

    override fun getStateFromMeta(meta: Int) =
        blockState.baseState.withProperty(LIMESTONE_STATES, BlockLimestone.LimestoneStates.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(LIMESTONE_STATES)?.ordinal ?: 0
}