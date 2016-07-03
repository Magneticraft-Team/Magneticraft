package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.INodeProvider
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.items.IItemHandler

@CapabilityInject(IItemHandler::class)
var ITEM_HANDLER: Capability<IItemHandler>? = null

@CapabilityInject(INodeProvider::class)
var NODE_PROVIDER: Capability<INodeProvider>? = null

fun registerCapabilities(){
    CapabilityManager.INSTANCE.register(INodeProvider::class.java, EmptyStorage(), { DefaultNodeProvider() })
}

fun <T> Capability<T>.fromTile(tile: TileEntity, side: EnumFacing? = null): T? {
    if (tile is ICapabilityProvider && tile.hasCapability(this, side)) {
        return tile.getCapability(this, side)
    }
    return null
}

class EmptyStorage<T> : Capability.IStorage<T> {

    override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? = NBTTagCompound()

    override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) {}
}

class DefaultNodeProvider : INodeProvider {

    override fun getNodes(): List<INode> = listOf()
}