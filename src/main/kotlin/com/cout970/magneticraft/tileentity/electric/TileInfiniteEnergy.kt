package com.cout970.magneticraft.tileentity.electric

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.api.energy.impl.ElectricNode
import com.cout970.magneticraft.util.TIER_1_MAX_VOLTAGE
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 27/07/2016.
 */
class TileInfiniteEnergy : TileElectricBase() {

    var mainNode = object : ElectricNode({ world }, { pos }, capacity = 1.25) {
        override fun getVoltage(): Double {
            return TIER_1_MAX_VOLTAGE
        }

        override fun applyCurrent(current: Double) {
            amperageCount += Math.abs(current) * 2
        }

        override fun applyPower(power: Double): Double = power
    }

    override val electricNodes: List<IElectricNode>
        get() = listOf(mainNode)

    override fun save(): NBTTagCompound = NBTTagCompound()

    override fun load(nbt: NBTTagCompound) {
    }
}