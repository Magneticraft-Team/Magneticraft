package com.cout970.magneticraft.systems.blocks

import net.minecraft.block.Block
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState

/**
 * Created by cout970 on 2017/01/28.
 */
interface IStatesEnum {
    val isVisible: Boolean
    val stateName: String
    val properties: List<IProperty<*>>
    val ordinal: Int

    fun getBlockState(block: Block): IBlockState

    companion object {
        val default = object : IStatesEnum {
            override val isVisible: Boolean = true
            override val stateName: String = "normal"
            override val properties: List<IProperty<*>> = emptyList()
            override val ordinal: Int = 0
            override fun getBlockState(block: Block): IBlockState = block.defaultState
        }
    }
}

