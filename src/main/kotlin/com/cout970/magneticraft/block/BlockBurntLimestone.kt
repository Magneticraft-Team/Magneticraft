package com.cout970.magneticraft.block

import coffee.cypher.mcextlib.extensions.items.stack
import com.cout970.magneticraft.block.states.LimestoneTypes
import com.cout970.magneticraft.block.states.OreTypes
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
 * Created by cout970 on 11/06/2016.
 */
object BlockBurntLimestone : BlockBase(Material.ROCK, "burnt_limestone") {

    override val inventoryVariants = LimestoneTypes.values().associate {
        it.ordinal to "type=${it.name}"
    }

    override fun damageDropped(state: IBlockState) = state.getValue(LIMESTONE_TYPE).ordinal

    override fun getItemName(stack: ItemStack?) =
        "${super.getItemName(stack)}_${LimestoneTypes.values()[stack?.metadata ?: 0].name.toLowerCase()}"

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (list == null || itemIn == null) {
            return
        }
        inventoryVariants.forEach { list += itemIn.stack(meta = it.key) }
    }

    override fun createBlockState() = BlockStateContainer(this, LIMESTONE_TYPE)

    override fun getStateFromMeta(meta: Int) =
        blockState.baseState.withProperty(LIMESTONE_TYPE, LimestoneTypes.values()[meta])

    override fun getMetaFromState(state: IBlockState?) =
        state?.getValue(LIMESTONE_TYPE)?.ordinal ?: 0
}