package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.INodeHandler
import net.minecraft.block.Block
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTBase
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import net.minecraftforge.common.capabilities.CapabilityManager
import net.minecraftforge.common.capabilities.ICapabilityProvider
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.items.IItemHandler

@CapabilityInject(IItemHandler::class)
var ITEM_HANDLER: Capability<IItemHandler>? = null

@CapabilityInject(INodeHandler::class)
var NODE_HANDLER: Capability<INodeHandler>? = null

@CapabilityInject(IManualConnectionHandler::class)
var MANUAL_CONNECTION_HANDLER: Capability<IManualConnectionHandler>? = null

@CapabilityInject(IFluidHandler::class)
var FLUID_HANDLER: Capability<IFluidHandler>? = null

fun registerCapabilities() {
    CapabilityManager.INSTANCE.register(INodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IManualConnectionHandler::class.java, EmptyStorage(), { DefaultManualConnectionHandler() })
}

fun <T> Capability<T>.fromTile(tile: TileEntity, side: EnumFacing? = null): T? {
    if (tile is ICapabilityProvider && tile.hasCapability(this, side)) {
        return tile.getCapability(this, side)
    }
    return null
}

fun <T> Capability<T>.fromBlock(block: Block, side: EnumFacing? = null): T? {
    if (block is ICapabilityProvider && block.hasCapability(this, side)) {
        return block.getCapability(this, side)
    }
    return null
}

class EmptyStorage<T> : Capability.IStorage<T> {

    override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? = NBTTagCompound()

    override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) = Unit
}

class DefaultNodeProvider : INodeHandler {

    override fun getNodes(): List<INode> = listOf()

    override fun getPos(): BlockPos = BlockPos.ORIGIN
}

class DefaultManualConnectionHandler : IManualConnectionHandler {

    override fun getBasePos(thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?, stack: ItemStack?): BlockPos? = thisBlock

    override fun connectWire(otherBlock: BlockPos?, thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?, stack: ItemStack?): Boolean = false
}