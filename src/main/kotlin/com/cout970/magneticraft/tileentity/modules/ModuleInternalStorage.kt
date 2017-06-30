package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.config.Config
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.interpolate
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/06/30.
 */
class ModuleInternalStorage(
        val electricModule: ModuleElectricity,
        val mainNode: IElectricNode,
        override val name: String = "module_electric_storage"
) : IModule {
    lateinit override var container: IModuleContainer

    companion object {
        //this is only used with voltage to charge the block, not for charging items
        val MAX_CHARGE_SPEED = 400
        val UPPER_LIMIT = 100.0
        val LOWER_LIMIT = 90.0
    }

    var energy: Int = 0
    val chargeRate = ValueAverage(20)

    override fun update() {
        if (world.isServer) {
            if (mainNode.voltage > UPPER_LIMIT) {
                val speed = interpolate(mainNode.voltage, UPPER_LIMIT, 120.0) * MAX_CHARGE_SPEED
                val finalSpeed = Math.min(Math.floor(speed).toInt(), Config.blockBatteryCapacity - energy)
                mainNode.applyPower(-finalSpeed.toDouble(), false)
                energy += finalSpeed
                chargeRate += finalSpeed
            } else if (mainNode.voltage < LOWER_LIMIT) {
                val speed = (1 - interpolate(mainNode.voltage, 60.0, LOWER_LIMIT)) * MAX_CHARGE_SPEED
                val finalSpeed = Math.min(Math.floor(speed).toInt(), energy)
                mainNode.applyPower(finalSpeed.toDouble(), false)
                energy -= finalSpeed
                chargeRate -= finalSpeed
            }
            chargeRate.tick()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        energy = nbt.getInteger("energy")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("energy", energy)
    }
}