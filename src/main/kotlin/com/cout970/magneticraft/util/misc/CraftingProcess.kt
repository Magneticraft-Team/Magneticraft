package com.cout970.magneticraft.util.misc

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
 * Created by cout970 on 22/08/2016.
 */
class CraftingProcess : INBTSerializable<NBTTagCompound> {

    var craft: () -> Unit
    var canCraft: () -> Boolean
    var useEnergy: (Float) -> Unit
    var limit: () -> Float
    var timer = 0f
    var lastTick = 0L

    constructor(craft: () -> Unit, canCraft: () -> Boolean, useEnergy: (Float) -> Unit, limit: () -> Float = { 100f }) {
        this.craft = craft
        this.canCraft = canCraft
        this.useEnergy = useEnergy
        this.limit = limit
    }

    fun tick(world: World, speed: Float) {
        if (canCraft()) {
            lastTick = world.totalWorldTime
            if (timer <= limit()) {
                timer += speed
                useEnergy(speed)
            }
            while (timer >= limit() && canCraft()) {
                timer -= limit()
                craft()
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound?): Unit = nbt!!.run {
        timer = getFloat("timer")
        lastTick = getLong("lastTick")
    }


    override fun serializeNBT(): NBTTagCompound = NBTTagCompound().apply {
        setFloat("timer", timer)
        setLong("lastTick", lastTick)
    }

    fun isWorking(world: World): Boolean = world.totalWorldTime - lastTick < 20
}