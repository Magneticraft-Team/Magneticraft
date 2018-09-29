package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.StringSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_AMOUNT_LIST
import com.cout970.magneticraft.systems.gui.DATA_ID_FLUID_NAME_LIST
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties

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

    override fun init() {
        tanks.forEach { it.setTileEntity(container.tile) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getCapability(cap: Capability<T>, facing: EnumFacing?): T? {
        return if (cap == FLUID_HANDLER) capabilityFilter(this, facing) as T else null
    }

    override fun drain(resource: FluidStack, doDrain: Boolean): FluidStack? {
        val tank = tanks.filter { it.canDrain() }.firstOrNull { it.drainInternal(resource, false) != null }
        return tank?.drainInternal(resource, doDrain)
    }

    override fun drain(maxDrain: Int, doDrain: Boolean): FluidStack? {
        val tank = tanks.filter { it.canDrain() }.firstOrNull { it.drainInternal(maxDrain, false) != null }
        return tank?.drainInternal(maxDrain, doDrain)
    }

    override fun fill(resource: FluidStack, doFill: Boolean): Int {
        val tank = tanks.filter { it.canFillFluidType(resource) }.firstOrNull { it.fillInternal(resource, false) != 0 }
        return tank?.fillInternal(resource, doFill) ?: 0
    }

    override fun getTankProperties(): Array<IFluidTankProperties> {
        return tanks.map { tank ->
            object : IFluidTankProperties {
                override fun canDrainFluidType(fluidStack: FluidStack?): Boolean = tank.canDrainFluidType(fluidStack)
                override fun getContents(): FluidStack? = tank.fluid
                override fun canFillFluidType(fluidStack: FluidStack?): Boolean = tank.canFillFluidType(fluidStack)
                override fun getCapacity(): Int = tank.capacity
                override fun canFill(): Boolean = tank.canFill()
                override fun canDrain(): Boolean = tank.canDrain()
            }
        }.toTypedArray()
    }

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
                { tank.fluid?.fluid?.name ?: "" }, { tank.clientFluidName = it })
        )
    }
}