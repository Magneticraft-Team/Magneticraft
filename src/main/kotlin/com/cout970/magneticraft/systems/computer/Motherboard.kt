package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.*
import gnu.trove.map.hash.TIntObjectHashMap

/**
 * Created by cout970 on 2016/09/30.
 */
class Motherboard(
    private val cpu: ICPU,
    private val ram: IRAM,
    private val rom: IROM
) : IMotherboard {

    companion object {
        const val CPU_START_POINT = 0xE000
    }

    var cyclesPerTick = 1_000_000 / 20 // 1MHz


    val deviceMap = TIntObjectHashMap<IDevice>()

    private val bus = Bus(ram) { deviceMap[it] }
    private var cpuCycles = -1
    private var clock = 0
    private var sleep = 0

    init {
        cpu.setMotherboard(this)
    }

    fun iterate() {
        if (sleep > 0) {
            sleep--
            return
        }
        if (cpuCycles >= 0) {
            cpuCycles += cyclesPerTick

            // Limits cycles if the CPU halts using sleep();
            if (cpuCycles > cyclesPerTick * 10) {
                cpuCycles = cyclesPerTick * 10
            }

            while (cpuCycles > 0) {
                cpuCycles--
                clock++
                cpu.iterate()
            }
        }
    }

    fun sleep(ticks: Int) {
        if (ticks > 0) {
            cpuCycles = 0
            sleep = ticks
        }
    }

    override fun start() {
        cpuCycles = 0
    }

    override fun halt() {
        cpuCycles = -1
    }

    override fun isOnline() = cpuCycles >= 0

    override fun reset() {
        clock = 0
        cpu.reset()
        deviceMap.values().filterIsInstance<IResettable>().forEach { it.reset() }
        rom.bios.use {
            var index = CPU_START_POINT
            while (true) {
                val r = it.read()
                if (r == -1) break

                ram.writeByte(index++, r.toByte())
            }
        }
    }

    override fun getBus(): IRW = bus

    override fun getCPU(): ICPU = cpu

    override fun getRAM(): IRAM = ram

    override fun getROM(): IROM = rom

    override fun getClock(): Int = clock

    override fun serialize(): Map<String, Any> {
        return mapOf(
            "sleep" to sleep,
            "cycles" to cpuCycles,
            "cpu" to cpu.serialize(),
            "ram" to ram.serialize()
        )
    }

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(map: Map<String, Any>) {
        sleep = map["sleep"] as Int
        cpuCycles = map["cycles"] as Int
        cpu.deserialize(map["cpu"] as Map<String, Any>)
        ram.deserialize(map["ram"] as Map<String, Any>)
    }
}