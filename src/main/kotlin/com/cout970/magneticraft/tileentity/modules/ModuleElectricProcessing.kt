package com.cout970.magneticraft.tileentity.modules

import com.cout970.magneticraft.gui.common.core.DATA_ID_BURNING_TIME
import com.cout970.magneticraft.gui.common.core.DATA_ID_MACHINE_PRODUCTION
import com.cout970.magneticraft.misc.crafting.ICraftingProcess
import com.cout970.magneticraft.misc.crafting.TimedCraftingProcess
import com.cout970.magneticraft.misc.gui.ValueAverage
import com.cout970.magneticraft.misc.network.FloatSyncVariable
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.tileentity.core.IModuleContainer
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/07/01.
 */
class ModuleElectricProcessing(
        val craftingProcess: ICraftingProcess,
        val storage: ModuleInternalStorage,
        val workingRate: Float,
        val costPerTick: Float,
        override val name: String = "module_electric_processing"
) : IModule {

    override lateinit var container: IModuleContainer

    val timedProcess = TimedCraftingProcess(craftingProcess, this::onWorkingTick)
    val production = ValueAverage()
    var working = false

    override fun update() {
        val fullPercentage = storage.energy.toFloat() / storage.capacity
        val rate = workingRate * fullPercentage
        //making sure that (speed * costPerTick) is an integer
        val speed = Math.floor((rate * costPerTick).toDouble()).toFloat() / costPerTick
        if (speed > 0) {
            timedProcess.tick(world, speed)
        }
        val isWorking = timedProcess.isWorking(world)
        if (isWorking != working) {
            working = isWorking
            container.sendUpdateToNearPlayers()
        }
        production.tick()
    }

    fun onWorkingTick(speed: Float) {
        production.add(speed * costPerTick)
        storage.energy = Math.max(0, storage.energy - (speed * costPerTick).toInt())
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
            FloatSyncVariable(DATA_ID_BURNING_TIME, getter = { timedProcess.timer },
                    setter = { timedProcess.timer = it }),
            production.toSyncVariable(DATA_ID_MACHINE_PRODUCTION)
    )
}