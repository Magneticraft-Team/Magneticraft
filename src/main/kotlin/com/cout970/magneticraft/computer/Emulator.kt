package com.cout970.magneticraft.computer

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.core.ITileRef
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.WindowConstants

/**
 * Created by cout970 on 2017/06/23.
 */

fun main(args: Array<String>) {

    val monitor = DeviceMonitor(FakeRef)

    val cpu = CPU_MIPS()
    val memory = RAM(0xFFFF + 1, false)
    val rom = ROM("assets/$MOD_ID/cpu/bios.bin")
    val bus = Bus(memory, mutableMapOf())
    val motherboard = Motherboard(cpu, memory, rom, bus)
    val mbDevice = DeviceMotherboard(FakeRef, motherboard)

    bus.devices.put(0xFF, mbDevice)
    bus.devices.put(0x00, monitor)

//    println(rom.bios.reader(Charsets.US_ASCII).readText())
//    rom.bios.buffered().iterator().apply {
//        var pc = 0
//        while (hasNext()) {
//            var tmp = 0
//            tmp = tmp.splitSet(0, nextByte())
//            tmp = tmp.splitSet(1, nextByte())
//            tmp = tmp.splitSet(2, nextByte())
//            tmp = tmp.splitSet(3, nextByte())
//            println("0x%08x  ".format(pc) + CPU_MIPS.decompileInst(tmp))
//            pc += 4
//        }
//    }

    val display = MonitorWindow(monitor)
    val window = JFrame("Emulator")
    window.contentPane = display
    window.setSize(800, 600)
    window.isVisible = true
    window.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

    println("Start")
    motherboard.reset()
    cpu.debugLevel = 2
    motherboard.cyclesPerTick = 200000
    motherboard.start()
    while (motherboard.isOnline()) {
        motherboard.iterate()
        display.revalidate()
        display.repaint()
    }
    println("End")
}

class MonitorWindow(val monitor: DeviceMonitor) : JPanel() {
    init {
        setSize(800, 600)
        background = Color.BLACK
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        val graphics = g as? Graphics2D ?: return
        val lines = monitor.lines
        val columns = monitor.columns

        graphics.color = Color.GREEN
//        graphics.fillRect(-10, -10, 10, 10)
        for (line in 0..lines - 1) {
            for (column in 0..columns - 1) {
                val character = monitor.getChar(line * columns + column) and 0xFF
                if (character != 0x20) {
                    val x = 12 * column + 20
                    val y = 14 * line + 20
                    graphics.drawString("" + character.toChar(), x, y)
                }
            }
        }
    }
}

object FakeRef : ITileRef {
    override fun getWorld(): World = error("Not available in emulation mode")
    override fun getPos(): BlockPos = error("Not available in emulation mode")
}