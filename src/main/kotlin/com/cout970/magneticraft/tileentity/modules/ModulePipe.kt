package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.fromTile
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.tileentity.modules.pipe.*
import com.cout970.magneticraft.util.vector.plus
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * Created by cout970 on 2017/08/28.
 */
class ModulePipe(
        val type: PipeType,
        override val name: String = "module_pipe"
) : IModule, INetworkNode {

    override lateinit var container: IModuleContainer

    var pipeNetwork: PipeNetwork? = null

    //@formatter:off
    override var network: Network<*>?
        set(it) { pipeNetwork = it as PipeNetwork }
        get() = pipeNetwork
    //@formatter:on

    override val ref: ITileRef get() = container.ref
    override val sides: List<EnumFacing> = EnumFacing.values().toList()

    override fun update() {
        if(world.isServer){
            if (network == null){
                network = PipeNetwork.createNetwork(this).apply { expand() }
            }
        }
    }

    fun getAdjacentTanksExcluding(exPos: BlockPos, exSide: EnumFacing?): List<IFluidHandler>{
        return EnumFacing.values().mapNotNull { side ->
            if(pos == exPos && side == exSide) return@mapNotNull null
            val tile = world.getTileEntity(pos + side) ?: return@mapNotNull null
            val tank = FLUID_HANDLER!!.fromTile(tile, side.opposite) ?: return@mapNotNull null
            if(tank == network) return@mapNotNull null
            return@mapNotNull tank
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        if(facing == null) return null
        return if (cap == FLUID_HANDLER) pipeNetwork?.let { PipeNetworkHandler(it, pos, facing) } as T else null
    }
}