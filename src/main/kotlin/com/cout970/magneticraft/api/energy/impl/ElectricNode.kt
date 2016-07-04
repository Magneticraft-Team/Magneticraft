package com.cout970.magneticraft.api.energy.impl

import com.cout970.magneticraft.api.energy.IElectricNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 11/06/2016.
 */
open class ElectricNode(
        private val resistance: Double = 0.01,
        private val worldGetter: () -> World,
        private val posGetter: () -> BlockPos
) : IElectricNode {

    private var voltage = 0.0
    private var amperage = 0.0
    private var amperageCount = 0.0

    override fun getAmperage() = amperage
    override fun getVoltage() = voltage
    override fun getResistance() = resistance

    override fun getWorld() = worldGetter.invoke()
    override fun getPos() = posGetter.invoke()

    override fun iterate() {
        amperage = amperageCount * 0.5
        amperageCount = 0.0
    }

    override fun applyCurrent(current: Double) {
        amperageCount += Math.abs(current)
        voltage += current
    }

    override fun applyPower(power: Double) {
        if (voltage > 1) {
            applyCurrent(power / voltage)
        } else {
            applyCurrent(power)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        if (nbt == null) return
        voltage = nbt.getDouble("V")
        amperage = nbt.getDouble("A")
    }

    override fun serializeNBT() = NBTTagCompound().apply {
        setDouble("V", voltage)
        setDouble("A", amperage)
    }
}