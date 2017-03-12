package com.cout970.magneticraft.block

import com.teamwizardry.librarianlib.common.base.block.BlockMod
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockOre : BlockMod("ore_block", Material.ROCK, *OreStates.values().map { it.getName() }.toTypedArray()) {

    lateinit var ORE_STATES: PropertyEnum<OreStates>
        private set

    init {
        soundType = SoundType.STONE
        setHardness(3.0F)
        setResistance(5.0F)
        setHarvestLevel("pickaxe", 1, defaultState.withProperty(ORE_STATES, OreStates.COPPER))
        setHarvestLevel("pickaxe", 1, defaultState.withProperty(ORE_STATES, OreStates.LEAD))
        setHarvestLevel("pickaxe", 2, defaultState.withProperty(ORE_STATES, OreStates.COBALT))
        setHarvestLevel("pickaxe", 2, defaultState.withProperty(ORE_STATES, OreStates.TUNGSTEN))
    }

    override fun damageDropped(state: IBlockState) = state.getValue(ORE_STATES).ordinal

    override fun createBlockState(): BlockStateContainer {
        ORE_STATES = PropertyEnum.create("ore", OreStates::class.java)!!
        return BlockStateContainer(this, ORE_STATES)
    }

    override fun getStateFromMeta(meta: Int) =
            blockState.baseState.withProperty(ORE_STATES, OreStates.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
            state?.getValue(ORE_STATES)?.ordinal ?: 0

    enum class OreStates : IStringSerializable {
        COPPER,
        LEAD,
        COBALT,
        TUNGSTEN;

        override fun getName() = name.toLowerCase()
    }
}

