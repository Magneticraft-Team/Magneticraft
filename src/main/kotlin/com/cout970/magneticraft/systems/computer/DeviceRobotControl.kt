package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.core.ITileRef

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
            ReadOnlyByte("requestStatus", { robot.status.ordinal.toByte() }),
            ReadOnlyByte("cooldown", { robot.cooldown.toByte() }),
            ReadOnlyByte("orientation", {robot.orientationFlag.toByte()}),
            ReadOnlyInt("batteryCapacity", { robot.batterySize }),
            ReadOnlyInt("batteryEnergy", { robot.batteryCharge }),
            ReadOnlyInt("failReason", { robot.failReason }),
            ReadOnlyInt("scanResult", { robot.scanResult })
    )
    //@formatter:on

    fun signal(data: Byte) {
        val signal = data.toInt()
        when (signal) {
            1 -> robot.move(true)
            2 -> robot.move(false)
            3 -> robot.rotateLeft()
            4 -> robot.rotateRight()
            5 -> robot.rotateUp()
            6 -> robot.rotateDown()
            7 -> robot.mine()
            8 -> robot.scan()
        }
    }

    override fun update() = Unit

    override fun readByte(bus: IRW, addr: Int): Byte = memStruct.read(addr)

    override fun writeByte(bus: IRW, addr: Int, data: Byte) = memStruct.write(addr, data)

    override fun serialize(): Map<String, Any> = mapOf()

    override fun deserialize(map: Map<String, Any>) = Unit
}