package com.cout970.magneticraft.misc.energy

import com.cout970.magneticraft.misc.tileentity.getCap
import com.cout970.magneticraft.registry.FORGE_ENERGY
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.energy.EnergyStorage

class RfStorage(cap: Int) : EnergyStorage(cap) {

    fun setEnergyStored(a: Int) {
        energy = a
    }

    fun exportTo(world: World, pos: BlockPos, facing: EnumFacing) {
        if (energyStored == 0) return

        val handler = world.getCap(FORGE_ENERGY, pos.offset(facing), facing.opposite) ?: return
        if (!handler.canReceive()) return

        val received = handler.receiveEnergy(energyStored, true)
        if (received > 0) {
            energyStored -= handler.receiveEnergy(received, false)
        }
    }
}