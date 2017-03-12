package com.cout970.magneticraft.tileentity.heat

import com.cout970.magneticraft.api.internal.heat.HeatContainer
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.tileentity.ITileTrait
import com.cout970.magneticraft.misc.tileentity.TraitHeat
import com.cout970.magneticraft.registry.FLUID_HANDLER
import com.cout970.magneticraft.tileentity.TileBase
import com.cout970.magneticraft.util.COPPER_HEAT_CAPACITY
import com.cout970.magneticraft.util.COPPER_MELTING_POINT
import com.cout970.magneticraft.util.DEFAULT_CONDUCTIVITY
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.fluids.FluidStack

/**
 * Created by cout970 on 04/07/2016.
 */
class TileHeatExchanger : TileBase() {

    val inputTank: Tank = object : Tank(4000) {
        override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
    }

    val outputTank: Tank = object : Tank(4000) {
        override fun canFillFluidType(fluid: FluidStack?): Boolean = fluid?.fluid?.name == "water"
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> getCapability(capability: Capability<T>, facing: EnumFacing?): T? {
        if (capability == FLUID_HANDLER) {
            return if (facing == EnumFacing.DOWN)  inputTank as T
            else outputTank as T
        }
        return super.getCapability(capability, facing)
    }

    override fun hasCapability(capability: Capability<*>, facing: EnumFacing?): Boolean {
        if (capability == FLUID_HANDLER) return true
        return super.hasCapability(capability, facing)
    }

    val heat = HeatContainer(dissipation = 0.0,
            specificHeat = COPPER_HEAT_CAPACITY * 3,
            maxHeat = COPPER_HEAT_CAPACITY * 3 * COPPER_MELTING_POINT,
            conductivity = DEFAULT_CONDUCTIVITY,
            worldGetter = { this.world },
            posGetter = { this.getPos() })

    val traitHeat: TraitHeat = TraitHeat(this, listOf(heat))

    override val traits: List<ITileTrait> = listOf(traitHeat)
}