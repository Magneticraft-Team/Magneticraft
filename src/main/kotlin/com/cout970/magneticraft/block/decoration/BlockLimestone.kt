package com.cout970.magneticraft.block.decoration

import com.cout970.magneticraft.block.BlockBase
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack
import net.minecraft.util.IStringSerializable

object BlockLimestone : BlockBase(Material.ROCK, "limestone") {

    lateinit var LIMESTONE_STATES: PropertyEnum<LimestoneStates>
        private set

    override val inventoryVariants = LimestoneStates.values().associate {
        it.ordinal to "type=${it.name}"
    }

    override fun getItemName(stack: ItemStack?) =
            "${super.getItemName(stack)}_${LimestoneStates.values()[stack?.metadata ?: 0].name.toLowerCase()}"

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

