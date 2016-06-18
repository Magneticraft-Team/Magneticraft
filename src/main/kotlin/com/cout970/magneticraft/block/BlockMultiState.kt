package com.cout970.magneticraft.block

import com.google.common.base.Joiner
import com.google.common.collect.Iterables
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockStateContainer
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
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

    private val COMMA_JOINER = Joiner.on(',')

    override fun getModelLoc(i :Int): ModelResourceLocation {
        return ModelResourceLocation(registryName, getStateName(deserializeState(i)))
    }

    override fun getStateFromMeta(meta: Int): IBlockState? = deserializeState(meta)

    override fun getMetaFromState(state: IBlockState?): Int = serializeState(state!!)

    override fun createBlockState(): BlockStateContainer? = BlockStateContainer(this, *getProperties())

    override fun damageDropped(state: IBlockState): Int {
        return serializeState(state)
    }

    @SideOnly(Side.CLIENT)
    override fun getSubBlocks(itemIn: Item?, tab: CreativeTabs?, list: MutableList<ItemStack>?) {
        if (itemIn == null || tab == null || list == null) return
        for (i in 0..16) {
            if (shouldItemBeDisplayed(itemIn, tab, i)) {
                list.add(ItemStack(itemIn, 1, i))
            }
        }
    }

    fun getStateName(state: IBlockState): String {
        return COMMA_JOINER.join(Iterables.transform(state.properties.entries, {
            if(it == null) {
                "NULL"
            }else{
                val pName = it.key.getName()
                @Suppress("UNCHECKED_CAST")
                val vName = (it.key as IProperty<Comparable<Any>>).getName(it.value as Comparable<Any>)
                "$pName=$vName"
            }
        }))
    }

    abstract fun shouldItemBeDisplayed(itemIn: Item, tab: CreativeTabs, i: Int): Boolean

    abstract fun getProperties(): Array<IProperty<*>>

    abstract fun deserializeState(meta: Int): IBlockState

    abstract fun serializeState(state: IBlockState): Int
}