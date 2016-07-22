package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.LimestoneTypes
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

val LIMESTONE_TYPE: PropertyEnum<LimestoneTypes> = PropertyEnum.create("type", LimestoneTypes::class.java)!!

object BlockLimestone : BlockBase(Material.ROCK, "limestone") {
    override val inventoryVariants = LimestoneTypes.values().associate {
        it.ordinal to "type=${it.name}"
    }

    //disabled for now
//    override fun damageDropped(state: IBlockState): Int {
//        val type = state.getValue(LIMESTONE_TYPE)
//        return when (type) {
//            LimestoneTypes.NORMAL -> LimestoneTypes.COBBLE.ordinal
//            else -> type.ordinal
//        }
//    }

    override fun getItemName(stack: ItemStack?) =
            "${super.getItemName(stack)}_${LimestoneTypes.values()[stack?.metadata ?: 0].name.toLowerCase()}"

    override fun createBlockState() = BlockStateContainer(this, LIMESTONE_TYPE)

    override fun getStateFromMeta(meta: Int) =
            blockState.baseState.withProperty(LIMESTONE_TYPE, LimestoneTypes.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
            state?.getValue(LIMESTONE_TYPE)?.ordinal ?: 0
}