package com.cout970.magneticraft.systems.tileentities

import com.cout970.magneticraft.api.core.ITileRef
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/06/29.
 */
data class StaticRef(
    internal val world: World,
    internal val pos: BlockPos
) : ITileRef {
    override fun getWorld(): World = world
    override fun getPos(): BlockPos = pos
}

data class ModuleContainerRef(
    val container: IModuleContainer
) : ITileRef {
    override fun getWorld(): World = container.world
    override fun getPos(): BlockPos = container.pos
}

data class DynamicTileRef(
    val tile: TileEntity
) : ITileRef {
    override fun getWorld(): World = tile.world
    override fun getPos(): BlockPos = tile.pos
}