package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.NBTTagCompound
import com.cout970.magneticraft.getCompoundTag
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.fluid.*
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.StringSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_AMOUNT_LIST
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_NAME_LIST
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

/**
 * Created by cout970 on 2017/07/13.
 */
class ModuleFluidHandler(
    vararg val tanks: Tank,
    val capabilityFilter: (IFluidHandler, EnumFacing?) -> IFluidHandler? = ALLOW_ALL,
    val guiSyncOffset: Int = 0,
    override val name: String = "module_fluid_handler"
) : IModule, IFluidHandler {

    override lateinit var container: IModuleContainer

    companion object {
        @JvmStatic
        val ALLOW_NONE: (IFluidHandler, EnumFacing?) -> IFluidHandler? = { _, _ -> null }
        @JvmStatic
        val ALLOW_ALL: (IFluidHandler, EnumFacing?) -> IFluidHandler? = { it, _ -> it }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return if (cap == FLUID_HANDLER) capabilityFilter(this, facing) as T else null
    }

    override fun drain(resource: FluidStack, doDrain: IFluidHandler.FluidAction): FluidStack {
        val tank = tanks.filter { it.allowOutput }.firstOrNull { it.drainSimulate(resource).isNotEmpty }
        return tank?.drain(resource, doDrain) ?: FluidStack.EMPTY
    }

    override fun drain(maxDrain: Int, doDrain: IFluidHandler.FluidAction): FluidStack {
        val tank = tanks.filter { it.allowOutput }.firstOrNull { it.drainSimulate(maxDrain).isNotEmpty }
        return tank?.drain(maxDrain, doDrain) ?: FluidStack.EMPTY
    }

    override fun fill(resource: FluidStack, doFill: IFluidHandler.FluidAction): Int {
        val tank = tanks.filter { it.allowInput }.firstOrNull { it.fillSimulate(resource) != 0 }
        return tank?.fill(resource, doFill) ?: 0
    }

    override fun getTankCapacity(tank: Int): Int = tanks[tank].capacity

    override fun getFluidInTank(tank: Int): FluidStack = tanks[tank].fluid

    override fun getTanks(): Int = tanks.size

    override fun isFluidValid(tank: Int, stack: FluidStack): Boolean = tanks[tank].allowInput && tanks[tank].fluidFilter(stack)

    override fun serializeNBT(): NBTTagCompound = newNbt {
        tanks.forEachIndexed { index, tank ->
            add("tank$index", tank.writeToNBT(NBTTagCompound()))
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        tanks.forEachIndexed { index, tank ->
            tank.readFromNBT(nbt.getCompoundTag("tank$index"))
        }
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = tanks.withIndex().flatMap { (index, tank) ->
        listOf(
            IntSyncVariable(DATA_ID_FLUID_AMOUNT_LIST[index + guiSyncOffset],
                { tank.fluidAmount }, { tank.clientFluidAmount = it }),

            StringSyncVariable(DATA_ID_FLUID_NAME_LIST[index + guiSyncOffset],
                { tank.fluid.orNull()?.fluid?.registryName?.toString() ?: "" }, { tank.clientFluidName = it })
        )
    }
}