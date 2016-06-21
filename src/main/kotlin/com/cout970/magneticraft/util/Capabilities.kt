package com.cout970.magneticraft.util

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.ICapabilityProvider


fun <T> Capability<T>.fromTile(tile: TileEntity, side: EnumFacing? = null): T? {
    if (tile is ICapabilityProvider && tile.hasCapability(this, side)) {
        return tile.getCapability(this, side)
    }
    return null
}