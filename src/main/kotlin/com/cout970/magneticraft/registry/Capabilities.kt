package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.core.INode
import com.cout970.magneticraft.api.core.INodeHandler
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.api.energy.IElectricConnection
import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.api.tool.IGear
import com.cout970.magneticraft.api.tool.IHammer
import com.cout970.magneticraft.item.ComputerItems
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
import net.minecraftforge.energy.IEnergyStorage
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandlerItem
import net.minecraftforge.items.IItemHandler

/**
 * Stores instances of Capabilities
 * to use them you need to add !! at the end
 * for example:
 * ITEM_HANDLER!!.fromTile(tile)
 */

@CapabilityInject(IItemHandler::class)
var ITEM_HANDLER: Capability<IItemHandler>? = null

@CapabilityInject(IElectricNodeHandler::class)
var ELECTRIC_NODE_HANDLER: Capability<IElectricNodeHandler>? = null

@CapabilityInject(IHeatNodeHandler::class)
var HEAT_NODE_HANDLER: Capability<IHeatNodeHandler>? = null

@CapabilityInject(IManualConnectionHandler::class)
var MANUAL_CONNECTION_HANDLER: Capability<IManualConnectionHandler>? = null

@CapabilityInject(IFluidHandler::class)
var FLUID_HANDLER: Capability<IFluidHandler>? = null

@CapabilityInject(IFluidHandlerItem::class)
var ITEM_FLUID_HANDLER: Capability<IFluidHandlerItem>? = null

@CapabilityInject(IEnergyStorage::class)
var FORGE_ENERGY: Capability<IEnergyStorage>? = null

@CapabilityInject(IFloppyDisk::class)
var ITEM_FLOPPY_DISK: Capability<IFloppyDisk>? = null

@CapabilityInject(IHammer::class)
var ITEM_HAMMER: Capability<IHammer>? = null

@CapabilityInject(IGear::class)
var ITEM_GEAR: Capability<IGear>? = null

/**
 * This is called on the server and the client at preInit
 */
fun registerCapabilities() {
    CapabilityManager.INSTANCE.register(IElectricNodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IHeatNodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IManualConnectionHandler::class.java, EmptyStorage(), { DefaultManualConnectionHandler() })
    CapabilityManager.INSTANCE.register(IHammer::class.java, EmptyStorage(), { DefaultHammer() })
    CapabilityManager.INSTANCE.register(IGear::class.java, EmptyStorage(), { DefaultGear() })
    CapabilityManager.INSTANCE.register(IFloppyDisk::class.java, EmptyStorage(), {
        ComputerItems.FloppyDisk(
                ItemStack(ComputerItems.floppyDisk, 1, 0,
                        ComputerItems.createNBT( 128, true, true)
                )
        )
    })
}

/**
 * Extension functions to get capabilities from TileEntities, Blocks and Items
 */
fun <T> Capability<T>.fromTile(tile: TileEntity, side: EnumFacing? = null): T? {
    if (tile.hasCapability(this, side)) {
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

fun <T> Capability<T>.fromItem(tile: ItemStack): T? {
    if (tile.hasCapability(this, null)) {
        return tile.getCapability(this, null)
    }
    return null
}

fun <T> TileEntity.getOrNull(cap: Capability<T>?, side: EnumFacing?): T? {
    cap ?: return null
    if (this.hasCapability(cap, side)) {
        return this.getCapability(cap, side)
    }
    return null
}


/**
 * Empty implementation of IStorage
 * At some point this should be changed, or just ignored
 */
class EmptyStorage<T> : Capability.IStorage<T> {

    override fun writeNBT(capability: Capability<T>?, instance: T, side: EnumFacing?): NBTBase? = NBTTagCompound()

    override fun readNBT(capability: Capability<T>?, instance: T, side: EnumFacing?, nbt: NBTBase?) = Unit
}

/**
 * Default implementation of API interfaces, used to register Capabilities
 */
class DefaultNodeProvider : INodeHandler, IElectricNodeHandler, IHeatNodeHandler {

    override fun getNode(id: NodeID): INode? = null
    override fun getNodes(): List<INode> = listOf()
    override fun getRef(): ITileRef {
        throw NotImplementedError("DefaultNodeProvider doesn't have a parent TileEntity")
    }

    override fun getInputConnections(): MutableList<IElectricConnection> = mutableListOf()
    override fun getOutputConnections(): MutableList<IElectricConnection> = mutableListOf()

    override fun canConnect(thisNode: IElectricNode?, other: IElectricNodeHandler?, otherNode: IElectricNode?,
                            side: EnumFacing?): Boolean {
        return false
    }

    override fun addConnection(connection: IElectricConnection?, side: EnumFacing?, output: Boolean) = Unit
    override fun removeConnection(connection: IElectricConnection?) = Unit
    override fun getConnections(): MutableList<IHeatConnection> = mutableListOf()
    override fun addConnection(connection: IHeatConnection?) = Unit
    override fun removeConnection(connection: IHeatConnection?) = Unit
}

class DefaultManualConnectionHandler : IManualConnectionHandler {

    override fun getBasePos(thisBlock: BlockPos, world: World, player: EntityPlayer, side: EnumFacing,
                            stack: ItemStack): BlockPos? = thisBlock

    override fun connectWire(otherBlock: BlockPos, thisBlock: BlockPos, world: World, player: EntityPlayer,
                             side: EnumFacing, stack: ItemStack): Boolean = false
}
class DefaultHammer : IHammer {

    override fun getMiningLevel(): Int = 0
    override fun getBreakingSpeed(): Int = 0
    override fun applyDamage(item: ItemStack, player: EntityPlayer): ItemStack = item
}

class DefaultGear : IGear{

    override fun getSpeedMultiplier(): Float = 1f
    override fun getMaxDurability(): Int = 0
    override fun getDurability(): Int = 0
    override fun applyDamage(): ItemStack = ItemStack.EMPTY
}