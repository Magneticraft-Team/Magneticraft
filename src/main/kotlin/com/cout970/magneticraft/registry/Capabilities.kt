package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.energy.*
import com.cout970.magneticraft.api.energy.item.IEnergyConsumerItem
import com.cout970.magneticraft.api.energy.item.IEnergyProviderItem
import com.cout970.magneticraft.api.energy.item.IEnergyStorageItem
import com.cout970.magneticraft.api.heat.IHeatConnection
import com.cout970.magneticraft.api.heat.IHeatNodeHandler
import com.cout970.magneticraft.item.ItemFloppyDisk
import net.darkhax.tesla.api.ITeslaConsumer
import net.darkhax.tesla.api.ITeslaHolder
import net.darkhax.tesla.api.ITeslaProducer
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

@CapabilityInject(ITeslaConsumer::class)
var TESLA_CONSUMER: Capability<ITeslaConsumer>? = null

@CapabilityInject(ITeslaProducer::class)
var TESLA_PRODUCER: Capability<ITeslaProducer>? = null

@CapabilityInject(ITeslaHolder::class)
var TESLA_STORAGE: Capability<ITeslaHolder>? = null

@CapabilityInject(IEnergyConsumerItem::class)
var ITEM_ENERGY_CONSUMER: Capability<IEnergyConsumerItem>? = null

@CapabilityInject(IEnergyProviderItem::class)
var ITEM_ENERGY_PROVIDER: Capability<IEnergyProviderItem>? = null

@CapabilityInject(IEnergyStorageItem::class)
var ITEM_ENERGY_STORAGE: Capability<IEnergyStorageItem>? = null

@CapabilityInject(IFloppyDisk::class)
var ITEM_FLOPPY_DISK: Capability<IFloppyDisk>? = null

/**
 * This is called on the server and the client at preInit
 */
fun registerCapabilities() {
    CapabilityManager.INSTANCE.register(IElectricNodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IHeatNodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IEnergyConsumerItem::class.java, EmptyStorage(), { DefaultItemEnergyConsumer() })
    CapabilityManager.INSTANCE.register(IEnergyProviderItem::class.java, EmptyStorage(), { DefaultItemEnergyProvider() })
    CapabilityManager.INSTANCE.register(IEnergyStorageItem::class.java, EmptyStorage(), { DefaultItemEnergyStorage() })
    CapabilityManager.INSTANCE.register(IManualConnectionHandler::class.java, EmptyStorage(), { DefaultManualConnectionHandler() })
    CapabilityManager.INSTANCE.register(IFloppyDisk::class.java, EmptyStorage(), {
        ItemFloppyDisk.FloppyDisk(ItemStack(ItemFloppyDisk, 1, 0, ItemFloppyDisk.createNBT("default", 128, true, true)))
    })
}

/**
 * Extension functions to get capabilities from TileEntities, Blocks and Items
 */
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

fun <T> Capability<T>.fromItem(tile: ItemStack): T? {
    if (tile is ICapabilityProvider && tile.hasCapability(this, null)) {
        return tile.getCapability(this, null)
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

    override fun getNodes(): List<INode> = listOf()
    override fun getPos(): BlockPos = BlockPos.ORIGIN
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

    override fun getBasePos(thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?,
                            stack: ItemStack?): BlockPos? = thisBlock

    override fun connectWire(otherBlock: BlockPos?, thisBlock: BlockPos?, world: World?, player: EntityPlayer?,
                             side: EnumFacing?, stack: ItemStack?): Boolean = false
}

class DefaultItemEnergyConsumer : IEnergyConsumerItem {

    override fun giveEnergy(power: Double, simulated: Boolean): Double = 0.0
}

class DefaultItemEnergyProvider : IEnergyProviderItem {

    override fun takeEnergy(power: Double, simulated: Boolean): Double = 0.0
}

class DefaultItemEnergyStorage : IEnergyStorageItem {

    override fun getStoredEnergy(): Double = 0.0

    override fun getCapacity(): Double = 0.0
}