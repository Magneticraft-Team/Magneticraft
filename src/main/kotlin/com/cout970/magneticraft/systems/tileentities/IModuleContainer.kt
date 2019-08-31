package com.cout970.magneticraft.systems.tileentities

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.network.IBD
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IModuleContainer {

    val tile: TileBase

    val world: World
    val pos: BlockPos

    val ref: ITileRef get() = ModuleContainerRef(this)

    val blockState: IBlockState
    val modules: List<IModule>

    fun markDirty()
    fun sendUpdateToNearPlayers()
    fun sendSyncDataToNearPlayers(ibd: IBD, distance: Double = 32.0)
}