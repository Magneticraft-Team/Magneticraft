package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.api.energy.IElectricNode
import com.cout970.magneticraft.misc.ConversionTable
import com.cout970.magneticraft.misc.ElectricConstants
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.tileentity.WorkingIndicator
import com.cout970.magneticraft.misc.tileentity.shouldTick
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/18.
 */
class ModuleSteamGenerator(
    val steamTank: Tank,
    val node: IElectricNode,
    val maxProduction: Int = 240,
    override val name: String = "module_steam_generator"
) : IModule {

    override lateinit var container: IModuleContainer

    companion object {
        const val STEAM_PER_OPERATION = 10
        const val ENERGY_PER_OPERATION = (STEAM_PER_OPERATION * ConversionTable.STEAM_TO_J).toInt()
    }

    val production = ValueAverage()
    var working = WorkingIndicator(this, 80)

    fun getAvailableOperations(): Int {
        if (steamTank.fluidAmount < STEAM_PER_OPERATION) return 0

        val fluidLimit = steamTank.fluidAmount / STEAM_PER_OPERATION

        if (node.voltage > ElectricConstants.TIER_1_GENERATORS_MAX_VOLTAGE) {
            return 0
        }

        return Math.min(fluidLimit, maxProduction / ENERGY_PER_OPERATION)
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

        if (working.working && container.shouldTick(20)) {
            container.sendUpdateToNearPlayers()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        working.deserializeNBT(nbt)
        production.storage = nbt.getFloat("production")
    }

    override fun serializeNBT(): NBTTagCompound {
        return working.serializeNBT().also {
            it.setFloat("production", production.average)
        }
    }
}