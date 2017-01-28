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
}