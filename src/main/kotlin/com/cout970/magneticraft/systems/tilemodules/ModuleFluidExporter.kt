package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.EnumFacing
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.fluid.fillExecute
import com.cout970.magneticraft.misc.fluid.fillSimulate
import com.cout970.magneticraft.misc.fluid.orNull
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer

import net.minecraft.util.math.BlockPos
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidHandler

class ModuleFluidExporter(
    val tank: Tank,
    val ports: () -> List<Pair<BlockPos, EnumFacing>>, // Facing is the side of the block to fill
    override val name: String = "module_fluid_exporter"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient) return
        val stack = tank.fluid.orNull() ?: return
        if (stack.amount <= 0) return

        for ((off, dir) in ports()) {
            val tile = world.getTileEntity(pos.add(off)) ?: continue
            val handler = tile.getOrNull(FLUID_HANDLER, dir) ?: continue

            val amount = handler.fillSimulate(stack)
            if (amount <= 0) continue

            handler.fillExecute(FluidStack(stack, amount))
            tank.drain(amount, IFluidHandler.FluidAction.EXECUTE)

            val newStack = tank.fluid.orNull() ?: return
            if (newStack.amount <= 0) return
        }
    }
}