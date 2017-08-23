package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.util.newNbt
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2017/08/22.
 */
class DeviceRobotControl(val tile: ITileRef, val robot: IMiningRobot) : IDevice, ITileRef by tile {

    //@formatter:off
    val memStruct = ReadWriteStruct("robot",
            ReadWriteStruct("device_header",
                    ReadOnlyByte("online", { 1 }),
                    ReadOnlyByte("type", { 3 }),
                    ReadOnlyShort("status", { 0 })
            ),

            ReadWriteByte("signal", { signal(it) }, { 0 }),
            ReadOnlyByte("request", { ((robot.requestedAction?.ordinal ?: -1) + 1).toByte() }),
            ReadOnlyByte("requestStatus", { robot.requestStatus.ordinal.toByte() }),
            ReadOnlyByte("cooldown", { robot.cooldown.toByte() }),
            ReadOnlyInt("batteryCapacity", { robot.batterySize }),
            ReadOnlyInt("batteryEnergy", { robot.batteryCharge })
    )
    //@formatter:on

    fun signal(data: Byte){
        val signal = data.toInt()
        when (signal) {
            1 -> robot.move(true)
            2 -> robot.move(false)
            3 -> robot.rotateLeft()
            4 -> robot.rotateRight()
            5 -> robot.rotateUp()
            6 -> robot.rotateDown()
            7 -> robot.mine()
        }
    }

    override fun readByte(addr: Int): Byte = memStruct.read(addr)

    override fun writeByte(addr: Int, data: Byte) = memStruct.write(addr, data)

    override fun deserializeNBT(nbt: NBTTagCompound?) = Unit

    override fun serializeNBT(): NBTTagCompound = newNbt { }

    override fun getId(): NodeID = NodeID("mining_robot", pos, world)
}