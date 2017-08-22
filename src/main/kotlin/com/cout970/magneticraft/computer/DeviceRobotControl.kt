package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/08/22.
 */
class DeviceRobotControl(val tile: ITileRef) : IDevice, ITileRef by tile {

    override fun readByte(addr: Int): Byte = 0

    override fun writeByte(addr: Int, data: Byte) = Unit

    override fun deserializeNBT(nbt: NBTTagCompound?) = Unit

    override fun serializeNBT(): NBTTagCompound = newNbt {  }

    override fun getId(): NodeID = NodeID("", pos, world)
}