package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.block.states.OreTypes
import net.minecraft.block.SoundType
import net.minecraft.block.material.Material
import net.minecraft.block.properties.PropertyEnum
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

/**
 * Created by cout970 on 12/05/2016.
 */
val ORE_TYPE = PropertyEnum.create("ore", OreTypes::class.java)

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

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(itemIn: Item, tab: CreativeTabs?, list: MutableList<ItemStack>) {
        inventoryVariants.forEach { list += itemIn.stack(meta = it.key) }
    }

    override fun createBlockState() = BlockStateContainer(this, ORE_TYPE)

    override fun getStateFromMeta(meta: Int) =
        blockState.baseState.withProperty(ORE_TYPE, OreTypes.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(ORE_TYPE)?.ordinal ?: 0
}