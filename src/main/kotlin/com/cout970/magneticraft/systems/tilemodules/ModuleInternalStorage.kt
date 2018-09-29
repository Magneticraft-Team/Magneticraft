package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.energy.IMachineEnergyInterface
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.interpolate
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.IntSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.systems.gui.DATA_ID_CHARGE_RATE
import com.cout970.magneticraft.systems.gui.DATA_ID_STORAGE
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/06/30.
 */
class ModuleInternalStorage(
    val mainNode: IElectricNode,
    val capacity: Int,
    val maxChargeSpeed: Double = 200.0,
    val upperVoltageLimit: Double = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
    val lowerVoltageLimit: Double = ElectricConstants.TIER_1_MACHINES_MIN_VOLTAGE,
    override val name: String = "module_electric_storage"
) : IModule, IMachineEnergyInterface {
    override lateinit var container: IModuleContainer

    companion object {
        const val INTERVAL = 10
    }

    var energy: Int = 0
    val chargeRate = ValueAverage(20)

    override fun update() {
        if (world.isServer) {
            if (mainNode.voltage > upperVoltageLimit) {
                val speed = interpolate(mainNode.voltage, upperVoltageLimit,
                    upperVoltageLimit + INTERVAL) * maxChargeSpeed

                val finalSpeed = Math.min(Math.floor(speed).toInt(), capacity - energy)
                if (finalSpeed != 0) {
                    mainNode.applyPower(-finalSpeed.toDouble(), false)
                    energy += finalSpeed
                    chargeRate += finalSpeed
                }
            } else if (mainNode.voltage < lowerVoltageLimit) {
                val speed = (1 - interpolate(mainNode.voltage, lowerVoltageLimit,
                    lowerVoltageLimit + INTERVAL)) * maxChargeSpeed

                val finalSpeed = Math.min(Math.floor(speed).toInt(), energy)
                if (finalSpeed != 0) {
                    mainNode.applyPower(finalSpeed.toDouble(), false)
                    energy -= finalSpeed
                    chargeRate -= finalSpeed
                }
            }
            chargeRate.tick()
        }
    }

    override fun getSpeed(): Double {
        return energy.toDouble() / capacity
    }

    override fun hasEnergy(amount: Double): Boolean = energy >= amount

    override fun useEnergy(amount: Double) {
        energy = Math.max(0, energy - amount.toInt())
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        energy = nbt.getInteger("energy")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("energy", energy)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        IntSyncVariable(DATA_ID_STORAGE, getter = { energy }, setter = { energy = it }),
        FloatSyncVariable(DATA_ID_CHARGE_RATE, getter = { chargeRate.average },
            setter = { chargeRate.storage = it })
    )
}