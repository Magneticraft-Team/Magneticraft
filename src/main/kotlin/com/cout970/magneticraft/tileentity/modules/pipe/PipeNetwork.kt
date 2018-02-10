package com.cout970.magneticraft.tileentity.modules.pipe

import com.cout970.magneticraft.misc.tileentity.getModule
import com.cout970.magneticraft.tileentity.core.TileBase
import com.cout970.magneticraft.tileentity.modules.ModulePipe
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

/**
 * Created by cout970 on 2017/08/28.
 */

class PipeNetwork(module: ModulePipe) : Network<ModulePipe>(
        module,
        getInspectFunc(module.type),
        Companion::createNetwork
) {

    var tankCache: List<IFluidHandler>? = null

    override fun clearCache() {
        tankCache = null
    }

    companion object {

        fun getInspectFunc(type: PipeType): InspectFunc {
            return func@ { tile, _ ->
                (tile as? TileBase)?.getModule<ModulePipe>()?.let {
                    if (it.type == type) listOf(it) else null
                } ?: emptyList()
            }
        }

        fun createNetwork(mod: ModulePipe): PipeNetwork = PipeNetwork(mod)
    }
}

class PipeNetworkHandler(val network: PipeNetwork, val exPos: BlockPos, val exSide: EnumFacing) : IFluidHandler {

    fun getTanks(): List<IFluidHandler> {

        if (network.tankCache == null) {
            network.tankCache = network
                    .members
                    .flatMap { it.getAdjacentTanksExcluding(exPos, exSide) }
                    .distinct()
                    .filter { if (it is PipeNetworkHandler) it.network != network else true }
        }
        return network.tankCache!!
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        val tank = getTanks().firstOrNull { it.drain(resource, false) != null }
        return tank?.drain(resource, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        val tank = getTanks().firstOrNull { it.drain(maxDrain, false) != null }
        return tank?.drain(maxDrain, doDrain)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        val tank = getTanks().firstOrNull { it.fill(resource, false) != 0 }
        return tank?.fill(resource, doFill) ?: 0
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return getTanks().flatMap { it.tankProperties.toList() }.toTypedArray()
    }
}

enum class PipeType(val maxRate: Int) {
    IRON(160),
    STEEL(320),
    TUNGSTEN(640)
}