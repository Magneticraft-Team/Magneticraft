package com.cout970.magneticraft.util.misc

import net.minecraft.nbt.NBTTagCompound
import net.minecraftforge.common.util.INBTSerializable

/**
 * Created by cout970 on 22/08/2016.
 */
class CraftingProcces : INBTSerializable<NBTTagCompound> {

    var craft: () -> Unit
    var canCraft: () -> Boolean
    var useEnergy: (Float) -> Unit
    var limit: Float
    var timer = 0f

    constructor(craft: () -> Unit, canCraft: () -> Boolean, useEnergy: (Float) -> Unit, limit: Float = 10f) {
        this.craft = craft
        this.canCraft = canCraft
        this.useEnergy = useEnergy
        this.limit = limit
    }

    fun tick(speed: Float) {
        if (canCraft()) {
            timer += speed
            if (timer > limit) {
                timer -= limit
                craft()
            }
            useEnergy(speed)
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?): Unit = nbt!!.run {
        timer = getFloat("timer")
        limit = getFloat("limit")
    }


    override fun serializeNBT(): NBTTagCompound = NBTTagCompound().apply {
        setFloat("timer", timer)
        setFloat("limit", limit)
    }
}