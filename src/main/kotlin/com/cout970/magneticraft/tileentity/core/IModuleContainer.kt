package com.cout970.magneticraft.tileentity.core

import com.cout970.magneticraft.api.core.ITileRef
import net.minecraft.block.state.IBlockState
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

interface IModuleContainer {

    val world: World
    val pos: BlockPos

    val ref: ITileRef get() = ModuleContainerRef(this)

    val blockState: IBlockState
    val modules: List<IModule>

    fun markDirty()
    fun sendUpdateToNearPlayers()
}