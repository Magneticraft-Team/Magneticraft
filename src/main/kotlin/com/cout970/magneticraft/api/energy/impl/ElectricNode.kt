package com.cout970.magneticraft.api.energy.impl

import com.cout970.magneticraft.api.energy.IElectricNode
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 11/06/2016.
 */
open class ElectricNode(resistance: Double, world: World, pos: BlockPos) : IElectricNode {

    private val world = world
    private val pos = pos
    private var voltage = 0.0
    private var amperage = 0.0
    private val resistance = resistance

    override fun getVoltage(): Double = voltage

    override fun getAmperage(): Double = amperage

    fun setVoltage(v: Double) {
        voltage = v
    }

    fun setAmperage(a: Double) {
        amperage = a
    }

    override fun getResistance(): Double = resistance

    override fun getWorld(): World = world

    override fun getPos(): BlockPos = pos

    override fun applyCurrent(current: Double) {
        amperage += Math.abs(current)
        voltage += current
    }

    override fun applyPower(power: Double) {
        if (voltage > 1) {
            applyCurrent(power / voltage)
        } else {
            applyPower(power)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        //null check for nbt
        if (nbt == null) return
        voltage = nbt.getDouble("V")
        amperage = nbt.getDouble("A")
    }

    override fun serializeNBT(): NBTTagCompound? {
        val nbt = NBTTagCompound()
        nbt.setDouble("V", voltage)
        nbt.setDouble("A", amperage)
        return nbt
    }
}