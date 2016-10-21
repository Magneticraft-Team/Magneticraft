package com.cout970.magneticraft.registry

import com.cout970.magneticraft.api.energy.IManualConnectionHandler
import com.cout970.magneticraft.api.energy.INode
import com.cout970.magneticraft.api.energy.INodeHandler
import com.cout970.magneticraft.api.energy.item.IEnergyConsumerItem
import com.cout970.magneticraft.api.energy.item.IEnergyProviderItem
import com.cout970.magneticraft.api.energy.item.IEnergyStorageItem
import com.cout970.magneticraft.api.heat.IHeatContainer
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

@CapabilityInject(IItemHandler::class)
var HEAT_HANDLER: Capability<IHeatContainer>? = null

@CapabilityInject(INodeHandler::class)
var NODE_HANDLER: Capability<INodeHandler>? = null

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

/**
 * This is called on the server and the client at preInit
 */
fun registerCapabilities() {
    CapabilityManager.INSTANCE.register(INodeHandler::class.java, EmptyStorage(), { DefaultNodeProvider() })
    CapabilityManager.INSTANCE.register(IEnergyConsumerItem::class.java, EmptyStorage(), { DefaultItemEnergyConsumer() })
    CapabilityManager.INSTANCE.register(IEnergyProviderItem::class.java, EmptyStorage(), { DefaultItemEnergyProvider() })
    CapabilityManager.INSTANCE.register(IEnergyStorageItem::class.java, EmptyStorage(), { DefaultItemEnergyStorage() })
    CapabilityManager.INSTANCE.register(IManualConnectionHandler::class.java, EmptyStorage(), { DefaultManualConnectionHandler() })
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
class DefaultNodeProvider : INodeHandler {

    override fun getNodes(): List<INode> = listOf()

    override fun getPos(): BlockPos = BlockPos.ORIGIN
}

class DefaultManualConnectionHandler : IManualConnectionHandler {

    override fun getBasePos(thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?, stack: ItemStack?): BlockPos? = thisBlock

    override fun connectWire(otherBlock: BlockPos?, thisBlock: BlockPos?, world: World?, player: EntityPlayer?, side: EnumFacing?, stack: ItemStack?): Boolean = false
}

class DefaultItemEnergyConsumer() : IEnergyConsumerItem {

    override fun giveEnergy(power: Double, simulated: Boolean): Double = 0.0
}

class DefaultItemEnergyProvider() : IEnergyProviderItem {

    override fun takeEnergy(power: Double, simulated: Boolean): Double = 0.0
}

class DefaultItemEnergyStorage() : IEnergyStorageItem {

    override fun getStoredEnergy(): Double = 0.0

    override fun getCapacity(): Double = 0.0
}