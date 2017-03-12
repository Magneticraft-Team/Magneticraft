package com.cout970.magneticraft.block.decoration

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.IStringSerializable

object BlockLimestone : BlockMod("limestone", Material.ROCK, *LimestoneStates.values().map { it.getName() }.toTypedArray()) {

    lateinit var LIMESTONE_STATES: PropertyEnum<LimestoneStates>
        private set


    override fun damageDropped(state: IBlockState) = state.getValue(BlockBurntLimestone.LIMESTONE_STATES).ordinal


    override fun createBlockState(): BlockStateContainer {
        LIMESTONE_STATES = PropertyEnum.create("type", LimestoneStates::class.java)!!
        return BlockStateContainer(this, LIMESTONE_STATES)
    }

    override fun getStateFromMeta(meta: Int) =
            defaultState.withProperty(LIMESTONE_STATES, LimestoneStates.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
            state?.getValue(LIMESTONE_STATES)?.ordinal ?: 0

    enum class LimestoneStates : IStringSerializable {
        NORMAL,
        BRICK,
        COBBLE;

        override fun getName() = name.toLowerCase()
    }
}

