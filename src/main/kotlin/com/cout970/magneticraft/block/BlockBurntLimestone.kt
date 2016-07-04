package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.LimestoneTypes
import net.minecraft.block.material.Material
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 11/06/2016.
 */
object BlockBurntLimestone : BlockBase(Material.ROCK, "burnt_limestone") {

    override val inventoryVariants = LimestoneTypes.values().associate {
        it.ordinal to "type=${it.name}"
    }

    override fun damageDropped(state: IBlockState) = state.getValue(LIMESTONE_TYPE).ordinal

    override fun getItemName(stack: ItemStack?) =
        "${super.getItemName(stack)}_${LimestoneTypes.values()[stack?.metadata ?: 0].name.toLowerCase()}"

    override fun createBlockState() = BlockStateContainer(this, LIMESTONE_TYPE)

    override fun getStateFromMeta(meta: Int) =
        blockState.baseState.withProperty(LIMESTONE_TYPE, LimestoneTypes.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(LIMESTONE_TYPE)?.ordinal ?: 0
}