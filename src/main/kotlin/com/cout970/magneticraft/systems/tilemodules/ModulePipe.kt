package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.Debug
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.internal.registries.tool.wrench.WrenchRegistry
import com.cout970.magneticraft.features.fluid_machines.Blocks
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.misc.vector.containsPoint
import com.cout970.magneticraft.misc.vector.plus
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.systems.blocks.IOnActivated
import com.cout970.magneticraft.systems.blocks.OnActivatedArgs
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import com.cout970.magneticraft.systems.tileentities.TileBase
import com.cout970.magneticraft.systems.tilemodules.pipe.INetworkNode
import com.cout970.magneticraft.systems.tilemodules.pipe.Network
import com.cout970.magneticraft.systems.tilemodules.pipe.PipeNetwork
import com.cout970.magneticraft.systems.tilemodules.pipe.PipeType
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidUtil
import net.minecraftforge.fluids.capability.templates.FluidHandlerConcatenate

/**
 * Created by cout970 on 2017/08/28.
 */
class ModulePipe(
    val tank: Tank,
    val type: PipeType,
    override val name: String = "module_pipe"
) : IModule, INetworkNode, IOnActivated {

    override lateinit var container: IModuleContainer

    enum class ConnectionState { PASSIVE, DISABLE, ACTIVE }
    enum class ConnectionType { NONE, PIPE, TANK }

    val connectionStates = Array(6) { ConnectionState.PASSIVE }
    var pipeNetwork: PipeNetwork? = null

    //@formatter:off
    override var network: Network<*>?
        set(it) { pipeNetwork = it as? PipeNetwork }
        get() = pipeNetwork
    //@formatter:on

    override val ref: ITileRef get() = container.ref

    override val sides: List<EnumFacing> = EnumFacing.values()
        .filter { connectionStates[it.ordinal] != ConnectionState.DISABLE }.toList()

    fun getConnectionType(side: EnumFacing, render: Boolean): ConnectionType {
        val tile = world.getTileEntity(pos + side) ?: return ConnectionType.NONE

        if (tile is TileBase && tile.getModule<ModulePipe>() != null) {
            val mod = tile.getModule<ModulePipe>()

            if (mod != null && mod.type == type) {
                if (mod.connectionStates[side.opposite.ordinal] == ConnectionState.DISABLE) {
                    return ConnectionType.NONE
                }
                if (render && (connectionStates[side.ordinal] == ConnectionState.DISABLE || mod.connectionStates[side.opposite.ordinal] == ConnectionState.DISABLE)) {
                    return ConnectionType.NONE
                }
                return ConnectionType.PIPE
            }
        } else {
            val handler = FLUID_HANDLER!!.fromTile(tile, side.opposite)
            if (handler != null) {
                if (render && connectionStates[side.ordinal] == ConnectionState.DISABLE) {
                    return ConnectionType.NONE
                }
                return ConnectionType.TANK
            }
        }

        return ConnectionType.NONE
    }

    override fun update() {
        if (world.isClient && !Debug.DEBUG) return

        if (pipeNetwork == null) {
            pipeNetwork = PipeNetwork.createNetwork(this).apply { expand() }
        }

        if (world.isClient) return

        enumValues<EnumFacing>().forEach { side ->
            val state = connectionStates[side.ordinal]
            if (state == ConnectionState.DISABLE) return@forEach

            val tile = world.getTileEntity(pos + side) ?: return@forEach
            val handler = FLUID_HANDLER!!.fromTile(tile, side.opposite) ?: return@forEach

            if (state == ConnectionState.PASSIVE) {
                if (FluidUtil.tryFluidTransfer(handler, getNetworkTank(), type.maxRate, false) != null) {
                    FluidUtil.tryFluidTransfer(handler, getNetworkTank(), type.maxRate, true)
                }
            } else if (state == ConnectionState.ACTIVE) {
                if (FluidUtil.tryFluidTransfer(getNetworkTank(), handler, type.maxRate, false) != null) {
                    // This line doesn't work after using hotswap, for some reason
                    FluidUtil.tryFluidTransfer(getNetworkTank(), handler, type.maxRate, true)
                }
            }
        }
    }

    override fun onActivated(args: OnActivatedArgs): Boolean {
        if (args.heldItem.isEmpty || !WrenchRegistry.isWrench(args.heldItem)) return false

        val boxes = Blocks.fluidPipeSides(args.worldIn, args.pos)
        val side = boxes.find { it.second.containsPoint(args.hit) }?.first ?: return false

        if (args.worldIn.isServer) {
            val type = getConnectionType(side, false)
            if (type == ConnectionType.PIPE) {
                if (connectionStates[side.ordinal] != ConnectionState.DISABLE) {

                    connectionStates[side.ordinal] = ConnectionState.DISABLE
                } else {
                    connectionStates[side.ordinal] = ConnectionState.PASSIVE
                }
            } else {
                connectionStates[side.ordinal] = connectionStates[side.ordinal].next()
            }
            container.sendUpdateToNearPlayers()
        }
        return true
    }

    fun ConnectionState.next(): ConnectionState = when (this) {
        ConnectionState.PASSIVE -> ConnectionState.ACTIVE
        ConnectionState.ACTIVE -> ConnectionState.DISABLE
        ConnectionState.DISABLE -> ConnectionState.PASSIVE
    }

    fun getNetworkTank() = FluidHandlerConcatenate(pipeNetwork?.members?.map { it.tank } ?: emptyList())

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if (cap == FLUID_HANDLER && facing != null && connectionStates[facing.ordinal] == ConnectionState.PASSIVE) {
            return getNetworkTank() as T
        }
        return null
    }

    override fun onBreak() {
        if (world.isServer) {
            container.tile.invalidate()
        }
        pipeNetwork?.split(this)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        connectionStates.indices.forEach {
            connectionStates[it] = ConnectionState.values()[nbt.getInteger(it.toString())]
        }
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        connectionStates.forEachIndexed { index, state ->
            add(index.toString(), state.ordinal)
        }
    }
}