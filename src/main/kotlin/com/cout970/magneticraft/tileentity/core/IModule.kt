package com.cout970.magneticraft.tileentity.core

import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable

interface IModule : INBTSerializable<NBTTagCompound> {

    val name: String
    var container: IModuleContainer

    fun init() = Unit
    fun update() = Unit
    fun onLoad() = Unit
    fun onBreak() = Unit

    fun <T : Any?> getCapability(cap: Capability<T>, facing: EnumFacing?): T? = null
    fun hasCapability(cap: Capability<*>, facing: EnumFacing?): Boolean = getCapability(cap, facing) != null

    override fun deserializeNBT(nbt: NBTTagCompound) = Unit
    override fun serializeNBT(): NBTTagCompound = newNbt {}
}