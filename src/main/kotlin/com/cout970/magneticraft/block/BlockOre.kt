package com.cout970.magneticraft.block

import com.cout970.magneticraft.block.states.OreTypes
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.item.ItemStack

/**
 * Created by cout970 on 12/05/2016.
 */
val ORE_TYPE: PropertyEnum<OreTypes> = PropertyEnum.create("ore", OreTypes::class.java)!!

object BlockOre : BlockBase(
    material = Material.ROCK,
    registryName = "ore_block"
) {
    override val inventoryVariants = OreTypes.values().associate {
        it.ordinal to "ore=${it.name}"
    }

    init {
        soundType = SoundType.STONE
    }

    override fun damageDropped(state: IBlockState) = state.getValue(ORE_TYPE).ordinal

    override fun getItemName(stack: ItemStack?) =
        "${super.getItemName(stack)}_${OreTypes.values()[stack?.metadata ?: 0].name.toLowerCase()}"

    override fun createBlockState() = BlockStateContainer(this, ORE_TYPE)

    override fun getStateFromMeta(meta: Int) =
        blockState.baseState.withProperty(ORE_TYPE, OreTypes.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(ORE_TYPE)?.ordinal ?: 0
}