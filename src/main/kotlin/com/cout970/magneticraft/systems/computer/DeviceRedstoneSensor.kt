package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.vector.plus
import net.minecraft.util.EnumFacing
import net.minecraftforge.event.ForgeEventFactory
import java.util.*

class DeviceRedstoneSensor(val tile: ITileRef) : IDevice, ITileRef by tile {

    val outputs = IntArray(6)

    val memStruct = ReadWriteStruct("redstone_sensor_header",
        ReadWriteStruct("device_header",
            ReadOnlyByte("online") { 1 },
            ReadOnlyByte("type") { 4 },
            ReadOnlyShort("status") { 0 }
        ),
        ReadWriteStruct("pin_down",
            ReadOnlyInt("input") { getInput(0) },
            ReadWriteInt("output", { setOutput(0, it) }, { getOutput(0) })
        ),
        ReadWriteStruct("pin_up",
            ReadOnlyInt("input") { getInput(1) },
            ReadWriteInt("output", { setOutput(1, it) }, { getOutput(1) })
        ),
        ReadWriteStruct("pin_north",
            ReadOnlyInt("input") { getInput(2) },
            ReadWriteInt("output", { setOutput(2, it) }, { getOutput(2) })
        ),
        ReadWriteStruct("pin_south",
            ReadOnlyInt("input") { getInput(3) },
            ReadWriteInt("output", { setOutput(3, it) }, { getOutput(3) })
        ),
        ReadWriteStruct("pin_west",
            ReadOnlyInt("input") { getInput(4) },
            ReadWriteInt("output", { setOutput(4, it) }, { getOutput(4) })
        ),
        ReadWriteStruct("pin_east",
            ReadOnlyInt("input") { getInput(5) },
            ReadWriteInt("output", { setOutput(5, it) }, { getOutput(5) })
        )
    )

    fun getInput(side: Int): Int {
        val facing = EnumFacing.getFront(side)

        return if (tile.world.getRedstonePower(pos, facing) > 0) -1 else 0
    }

    fun getOutput(side: Int): Int {
        return outputs[side]
    }

    fun setOutput(side: Int, value: Int) {
        outputs[side] = value

        val facing = EnumFacing.getFront(side)
        val blockState = world.getBlockState(pos)

        if (ForgeEventFactory.onNeighborNotify(world, pos, blockState, EnumSet.of(facing.opposite), false).isCanceled)
            return

        world.neighborChanged(pos + facing.opposite, blockState.block, pos)
    }

    override fun update() = Unit

    override fun writeByte(bus: IRW, addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    override fun readByte(bus: IRW, addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun serialize(): MutableMap<String, Any> {
        return mutableMapOf("outputs" to outputs.copyOf())
    }

    override fun deserialize(map: MutableMap<String, Any>) {
        System.arraycopy(map["outputs"] as IntArray, 0, outputs, 0, 6)
    }
}