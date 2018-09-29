package com.cout970.magneticraft.systems.tilemodules

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.crafting.ICraftingProcess
import com.cout970.magneticraft.misc.crafting.TimedCraftingProcess
import com.cout970.magneticraft.misc.fluid.Tank
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.misc.world.isClient
import com.cout970.magneticraft.systems.gui.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.systems.gui.DATA_ID_MACHINE_CONSUMPTION
import com.cout970.magneticraft.systems.tileentities.IModule
import com.cout970.magneticraft.systems.tileentities.IModuleContainer
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/01.
 */
class ModuleSteamProcessing(
    val craftingProcess: ICraftingProcess,
    val storage: Tank,
    val workingRate: Float,
    val costPerTick: Float,
    override val name: String = "module_electric_processing"
) : IModule {

    override lateinit var container: IModuleContainer

    val timedProcess = TimedCraftingProcess(craftingProcess, this::onWorkingTick)
    val consumption = ValueAverage()
    var working = false

    override fun update() {
        if (world.isClient) return

        val rate = workingRate * storage.fluidAmount / storage.capacity.toDouble()

        //making sure that (speed * costPerTick) is an integer
        val speed = Math.floor((rate * costPerTick)).toFloat() / costPerTick
        if (speed > 0 && speed * costPerTick.toDouble() <= storage.fluidAmount) {
            timedProcess.tick(world, speed)
        }

        val isWorking = timedProcess.isWorking(world)
        if (isWorking != working) {
            working = isWorking
            container.sendUpdateToNearPlayers()
        }
        consumption.tick()
    }

    fun onWorkingTick(speed: Float) {
        consumption += speed * costPerTick
        storage.drain((speed * costPerTick).toInt(), true)
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        timedProcess.deserializeNBT(nbt.getCompoundTag("timedProcess"))
        working = nbt.getBoolean("working")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("timedProcess", timedProcess.serializeNBT())
        add("working", working)
    }

    override fun getGuiSyncVariables(): List<SyncVariable> = listOf(
        FloatSyncVariable(DATA_ID_BURNING_TIME, { timedProcess.timer }, { timedProcess.timer = it }),
        consumption.toSyncVariable(DATA_ID_MACHINE_CONSUMPTION)
    )
}