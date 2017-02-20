package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.*
import com.cout970.magneticraft.misc.world.isServer
import com.cout970.magneticraft.tileentity.computer.TileComputer
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 2016/09/30.
 */
class Motherboard(private val cpu: ICPU, private val memory: IMemory, private val rom: IROM, val tile: TileComputer) : IMotherboard {

    val CPU_CLOCK = 1000000 / 20
    private var cpuCycles = -1
    private var clock = 0
    private var bus: Bus = Bus(memory, mutableMapOf(0xFF to DeviceMotherboard(tile, this)))
    private var sleep = 0

    init {
        cpu.setMotherboard(this)
    }

    fun attach(id: Int, dev: IDevice) {
        bus.devices.put(id, dev)
    }

    fun iterate() {
        if (sleep > 0) {
            sleep--
            return
        }
        if (cpuCycles >= 0) {
            cpuCycles += CPU_CLOCK

            //limits cycles if the CPU halts using sleep();
            if (cpuCycles > CPU_CLOCK * 10) {
                cpuCycles = CPU_CLOCK * 10
            }

            //DEBUG to measure the performance of the cpu
//            var nanos = System.nanoTime()
//            val cycles = cpuCycles

            while (cpuCycles > 0) {
                cpuCycles--
                clock++
                cpu.iterate()
            }

//            nanos = System.nanoTime() - nanos
//            debug("Cycles: %d, Time: %10.1f(ns), %5.1f(micro seg) %.1f(ms), %.1f(s)".format(cycles, nanos.toFloat(),
//                    nanos.toFloat() / 1000, nanos.toFloat() / (1000 * 1000), nanos.toFloat() / (1000 * 1000 * 1000)))
        }
    }

    fun sleep(ticks: Int) {
        if (ticks > 0) {
            cpuCycles = 0
            sleep = ticks
        }
    }

    override fun getBus(): Bus = bus

    override fun getCPU(): ICPU = cpu

    override fun getMemory(): IMemory = memory

    override fun getROM(): IROM = rom

    override fun getDevices(): MutableList<IDevice>? = bus.devices.values.toMutableList()

    override fun getClock(): Int = clock

    fun isOnline() = cpuCycles >= 0

    override fun halt() {
        cpuCycles = -1
    }

    override fun reset() {
        if (tile.world?.isServer ?: true) {
            clock = 0
            cpu.reset()
            rom.bios.use {
                var index = 0
                while (true) {
                    val r = it.read()
                    if (r == -1) break
                    memory.writeByte(index++, r.toByte())
                }
            }
        }
    }

    override fun start() {
        cpuCycles = 0
    }

    override fun deserializeNBT(nbt: NBTTagCompound?) {
        nbt?.setInteger("cycles", cpuCycles)
        cpu.deserializeNBT(nbt!!.getCompoundTag("cpu"))
        memory.deserializeNBT(nbt.getCompoundTag("ram"))
    }

    override fun serializeNBT(): NBTTagCompound {
        val nbt = NBTTagCompound()
        if (nbt.hasKey("cycles")) cpuCycles = nbt.getInteger("cycles")
        nbt.setTag("cpu", cpu.serializeNBT())
        nbt.setTag("ram", memory.serializeNBT())
        return nbt
    }
}