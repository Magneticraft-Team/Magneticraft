package com.cout970.magneticraft.misc.crafting

import com.cout970.magneticraft.NBTTagCompound
import com.cout970.magneticraft.misc.add
import com.cout970.magneticraft.misc.newNbt
import com.cout970.magneticraft.totalWorldTime
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
 * Created by cout970 on 22/08/2016.
 */
class TimedCraftingProcess(
    var process: ICraftingProcess,
    var onWorkingTick: (Float) -> Unit
) : INBTSerializable<NBTTagCompound> {

    var timer = 0f
    var lastTick = 0L

    fun tick(world: World, speed: Float) {
        if (process.canCraft(world)) {
            lastTick = world.totalWorldTime
            if (timer <= limit()) {
                timer += speed
                onWorkingTick(speed)
            }
            while (timer >= limit() && process.canCraft(world)) {
                timer -= limit()
                process.craft(world)
            }
        }
    }

    override fun deserializeNBT(nbt: NBTTagCompound): Unit = nbt.run {
        timer = getFloat("timer")
        lastTick = getLong("lastTick")
    }


    override fun serializeNBT(): NBTTagCompound = newNbt {
        add("timer", timer)
        add("lastTick", lastTick)
    }

    fun isWorking(world: World): Boolean = world.totalWorldTime - lastTick < 20

    fun limit(): Float = process.duration()
}