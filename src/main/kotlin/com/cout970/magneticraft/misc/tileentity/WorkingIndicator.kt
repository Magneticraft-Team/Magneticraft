package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.tileentity.core.IModule
import com.cout970.magneticraft.util.add
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

class WorkingIndicator(val module: IModule) : INBTSerializable<NBTTagCompound> {

    var working = false
    var lastWorkTick = 0L

    operator fun invoke(): Boolean = working

    fun onWork() {
        lastWorkTick = module.world.totalWorldTime
    }

    fun tick() {
        val isWorking = module.world.totalWorldTime - lastWorkTick < 20
        if (isWorking != working) {
            working = isWorking
            module.container.sendUpdateToNearPlayers()
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound) {
        working = nbt.getBoolean("working")
    }

    override fun serializeNBT(): NBTTagCompound = newNbt { add("working", working) }
}