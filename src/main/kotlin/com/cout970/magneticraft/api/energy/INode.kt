package com.cout970.magneticraft.api.energy

import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.util.INBTSerializable

/**
 * Created by cout970 on 11/06/2016.
 */
interface INode : INBTSerializable<NBTTagCompound>{

    fun getWorld(): World

    fun getPos(): BlockPos
}