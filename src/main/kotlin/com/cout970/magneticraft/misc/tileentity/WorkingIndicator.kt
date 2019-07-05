package com.cout970.magneticraft.misc.tileentity

import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.systems.tileentities.IModule
import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

class WorkingIndicator(val module: IModule, val freq: Int = 20) : INBTSerializable<NBTTagCompound> {

    var working = false
    var lastWorkTick = 0L

    operator fun invoke(): Boolean = working

    fun onWork() {
        lastWorkTick = module.world.totalWorldTime
    }

    fun tick() {
        val isWorking = module.world.totalWorldTime - lastWorkTick < freq
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