package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.registry.getOrNull
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import net.minecraft.util.EnumFacing
import net.minecraftforge.fluids.FluidStack

class ModuleFluidExporter(
        val tank: Tank,
        vararg val sides: EnumFacing,
        override val name: String = "module_fluid_exporter"
) : IModule {

    override lateinit var container: IModuleContainer

    override fun update() {
        if (world.isClient) return
        val stack = tank.fluid ?: return
        if (stack.amount <= 0) return

        for (dir in sides) {
            val tile = world.getTileEntity(pos.offset(dir)) ?: continue
            val handler = tile.getOrNull(FLUID_HANDLER, dir.opposite) ?: continue

            val amount = handler.fill(stack, false)
            if (amount <= 0) continue

            handler.fill(FluidStack(stack, amount), true)
            tank.drainInternal(amount, true)

            val newStack = tank.fluid ?: return
            if (newStack.amount <= 0) return
        }
    }
}