package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.IElectricNodeHandler
import com.cout970.magneticraft.misc.ElectricConstants
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.min

data class RFWrapper(val mod: IElectricNodeHandler) : IEnergyStorage {
    val electricNodes: List<IElectricNode> get() =  mod.nodes.filterIsInstance<IElectricNode>()

    override fun canExtract(): Boolean = false

    override fun getMaxEnergyStored(): Int = electricNodes.sumBy { (ElectricConstants.TIER_1_MAX_VOLTAGE * ElectricConstants.TIER_1_MAX_VOLTAGE * it.capacity).toInt() }

    override fun getEnergyStored(): Int = electricNodes.sumBy { (it.voltage * it.voltage * it.capacity).toInt() }

    override fun extractEnergy(maxExtract: Int, simulate: Boolean): Int = maxExtract

    override fun receiveEnergy(maxReceive: Int, simulate: Boolean): Int {
        if (electricNodes.isEmpty()) return 0
        val space = maxEnergyStored - energyStored

        return electricNodes.first()
                .applyPower(min(space, maxReceive).toDouble(), simulate).toInt()
    }

    override fun canReceive(): Boolean = true
}