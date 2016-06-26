package com.cout970.magneticraft.block

import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
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

abstract class BlockMultiState(material: Material, registryName: String, unlocalizedName: String = registryName) :
        BlockBase(material, registryName, unlocalizedName) {

    override fun getStateFromMeta(meta: Int): IBlockState? = getStateMap()[meta]

    override fun getMetaFromState(state: IBlockState?): Int {
        val meta = getStateMap().entries.indexOfFirst { it.value == state }
        return if(meta == -1) 0 else meta
    }

    override fun createBlockState(): BlockStateContainer? = BlockStateContainer(this, *getProperties())

    override fun damageDropped(state: IBlockState): Int {
        val meta = getMetaFromState(state)
        if(!isHiddenState(state, meta)){
            return meta
        }
        return 0
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (itemIn == null || tab == null || list == null) return
        for (i in getStateMap().entries) {
            if (!isHiddenState(i.value, i.key)) {
                list.add(ItemStack(itemIn, 1, i.key))
            }
        }
    }

    abstract fun isHiddenState(state: IBlockState, meta: Int): Boolean

    abstract fun getProperties(): Array<IProperty<*>>

    abstract fun getStateMap(): Map<Int, IBlockState>

    fun getStateName(state: IBlockState): String {
        return state.properties.entries.joinToString(separator = ",") {
            if (it == null) {
                "NULL"
            } else {
                val pName = it.key.getName()
                @Suppress("UNCHECKED_CAST")
                val vName = (it.key as IProperty<Comparable<Any>>).getName(it.value as Comparable<Any>)
                "$pName=$vName"
            }
        }
    }
}