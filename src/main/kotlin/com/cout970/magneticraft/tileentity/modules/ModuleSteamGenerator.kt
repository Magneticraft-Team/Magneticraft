package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.tileentity.WorkingIndicator
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.ConversionTable
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/18.
 */
class ModuleSteamGenerator(
    val steamTank: Tank,
    val node: IElectricNode,
    override val name: String = "module_steam_generator"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        const val MAX_ENERGY_PER_TICK = 240
        const val STEAM_PER_OPERATION = 10
        const val ENERGY_PER_OPERATION = (STEAM_PER_OPERATION * ConversionTable.STEAM_TO_J).toInt()
        const val MAX_OPERATIONS_PER_TICK = MAX_ENERGY_PER_TICK / ENERGY_PER_OPERATION
    }

    val production = ValueAverage()
    var working = WorkingIndicator(this)

    fun getAvailableOperations(): Int {
        if (steamTank.fluidAmount < STEAM_PER_OPERATION) return 0

        val fluidLimit = steamTank.fluidAmount / STEAM_PER_OPERATION

        if (node.voltage > ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE) {
            return 0
        }

        return Math.min(fluidLimit, MAX_OPERATIONS_PER_TICK)
    }

    override fun update() {
        if (world.isClient) return
        val operations = getAvailableOperations()
        if (operations > 0) {
            working.onWork()
            steamTank.drain(STEAM_PER_OPERATION * operations, true)
            node.applyPower(ENERGY_PER_OPERATION * operations.toDouble(), false)
            production += ENERGY_PER_OPERATION * operations
        }
        production.tick()
        working.tick()
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        working.deserializeNBT(nbt)
    }

    override fun serializeNBT(): NBTTagCompound = working.serializeNBT()
}