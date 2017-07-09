package com.cout970.magneticraft.computer

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.misc.network.IBD
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 2017/06/23.
 */

fun main(args: Array<String>) {

    val monitor = DeviceMonitor(FakeRef)

    val cpu = CPU_MIPS()
    val memory = RAM(0xFFFF + 1, false)
    val rom = ROM("assets/$MOD_ID/cpu/bios.bin")
    val bus = Bus(memory, mapOf(0xFF to monitor))
    val motherboard = Motherboard(cpu, memory, rom, bus)

//    println(rom.bios.reader(Charsets.US_ASCII).readText())

    println("Start")
    motherboard.reset()
    cpu.debugLevel = 2
    motherboard.cyclesPerTick = 200000
    motherboard.start()
    while (motherboard.isOnline()) {
        motherboard.iterate()
        val data = IBD()
        monitor.saveToClient(data)
        monitor.loadFromServer(data)
        printMonitor(monitor)
    }
    println("End")
}

private fun printMonitor(monitor: DeviceMonitor) {
    clearScreen()
    val lines = monitor.lines
    val columns = monitor.columns

    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
    for (line in 0..lines - 1) {
        for (column in 0..columns - 1) {
            val character = monitor.getChar(line * columns + column) and 0xFF
            print(character.toChar())
        }
        println()
    }
    println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++")
}

private fun clearScreen() {
//    ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor()
//    print("\u001b[H\u001b[2J")
//    System.out.flush()
}

private object FakeRef : ITileRef {
    override fun getWorld(): World = error("Not available in emulation mode")
    override fun getPos(): BlockPos = error("Not available in emulation mode")
}