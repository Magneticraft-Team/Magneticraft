package com.cout970.magneticraft.block

import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState

/**
 * Created by cout970 on 2017/01/28.
 */
interface IStatesEnum {
    val blockState: IBlockState
    val isVisible: Boolean
    val stateName: String
    val properties: List<IProperty<*>>
    val ordinal: Int

    companion object {

        fun createDefault(state: IBlockState) = object : IStatesEnum {
            override val blockState: IBlockState = state
            override val isVisible: Boolean = true
            override val stateName: String = "normal"
            override val properties: List<IProperty<*>> = emptyList()
            override val ordinal: Int = 0
        }
    }
}

