package com.cout970.magneticraft.util

import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World

/**
 * Created by cout970 on 29/06/2016.
 */

fun World.shouldTick(time: Int): Boolean {
    return totalWorldTime % time == 0L
}

fun TileEntity.shouldTick(time: Int): Boolean {
    return (world.totalWorldTime + pos.hashCode()) % time == 0L
}

operator fun <T : Comparable<T>> IProperty<T>.get(state: IBlockState): T = state.getValue(this)

fun <T : Comparable<T>> IProperty<T>.isIn(state: IBlockState): Boolean = this in state.properties

operator fun IElectricConnection.contains(node: IElectricNode) = this.firstNode == node || this.secondNode == node
