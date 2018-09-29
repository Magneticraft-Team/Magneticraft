package com.cout970.magneticraft.systems.tileentities

import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.misc.network.SyncVariable
import com.cout970.magneticraft.misc.newNbt
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.INBTSerializable
import net.minecraftforge.fml.relauncher.Side

interface IModule : INBTSerializable<NBTTagCompound> {

    val name: String

    var container: IModuleContainer

    val world: World get() = container.world
    val pos: BlockPos get() = container.pos

    fun init() = Unit
    fun update() = Unit
    fun onLoad() = Unit
    fun onBreak() = Unit

    fun <T : Any?> getCapability(cap: Capability<T>, facing: EnumFacing?): T? = null
    fun hasCapability(cap: Capability<*>, facing: EnumFacing?): Boolean = getCapability(cap, facing) != null

    override fun deserializeNBT(nbt: NBTTagCompound) = Unit
    override fun serializeNBT(): NBTTagCompound = newNbt {}

    fun receiveSyncData(ibd: IBD, otherSide: Side) = Unit

    fun getGuiSyncVariables(): List<SyncVariable> = emptyList()
}