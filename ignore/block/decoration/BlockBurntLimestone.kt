package com.cout970.magneticraft.block.decoration

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState

/**
 * Created by cout970 on 11/06/2016.
 */
object BlockBurntLimestone : BlockMod("burnt_limestone", Material.ROCK, *BlockLimestone.LimestoneStates.values().map { it.getName() }.toTypedArray()) {

    lateinit var LIMESTONE_STATES: PropertyEnum<BlockLimestone.LimestoneStates>
        private set

    override fun damageDropped(state: IBlockState) = state.getValue(LIMESTONE_STATES).ordinal


    override fun createBlockState(): BlockStateContainer {
        LIMESTONE_STATES = PropertyEnum.create("type", BlockLimestone.LimestoneStates::class.java)!!
        return BlockStateContainer(this, LIMESTONE_STATES)
    }

    override fun getStateFromMeta(meta: Int) =
        defaultState.withProperty(LIMESTONE_STATES, BlockLimestone.LimestoneStates.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(LIMESTONE_STATES)?.ordinal ?: 0

}