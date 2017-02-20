package com.cout970.magneticraft.misc.tileentity

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/02/20.
 */

fun TileEntity.shouldTick(time: Int): Boolean {
    return (world.totalWorldTime + pos.hashCode()) % time == 0L
}

inline fun <reified T : TileEntity> World.getTile(pos: BlockPos): T? {
    val tile = getTileEntity(pos)
    return if (tile is T) tile else null
}

inline fun <reified T : TileEntity> IBlockAccess.getTile(pos: BlockPos): T? {
    val tile = getTileEntity(pos)
    return if (tile is T) tile else null
}