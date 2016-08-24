package com.cout970.magneticraft.block

import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable

/**
 * Created by cout970 on 12/05/2016.
 */
object BlockOre : BlockBase(Material.ROCK, "ore_block") {

    override val inventoryVariants = OreStates.values().associate {
        it.ordinal to "ore=${it.name}"
    }

    lateinit var ORE_STATES: PropertyEnum<OreStates>
        private set

    init {
        soundType = SoundType.STONE
    }

    override fun damageDropped(state: IBlockState) = state.getValue(ORE_STATES).ordinal

    override fun getItemName(stack: ItemStack?) =
            "${super.getItemName(stack)}_${OreStates.values()[stack?.metadata ?: 0].name.toLowerCase()}"

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

