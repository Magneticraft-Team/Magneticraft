package cpu

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.computer.IROM
import com.cout970.magneticraft.computer.*
import com.cout970.magneticraft.misc.network.IBD
import java.awt.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.InputStream
import javax.swing.*

/**
 * Created by cout970 on 2017/06/23.
 */

private var timer = 0L
private var lastTick = 0
private var useOs = true

fun main(args: Array<String>) {

    val img = "shell"
    val osDisk = FakeFloppyDisk(File("./src/main/resources/assets/magneticraft/cpu/$img.bin"), true)
    val programDisk = FakeFloppyDisk(File("./run/disk.img"), false)

    val monitor = DeviceMonitor(FakeRef)
    val floppyDrive = DeviceFloppyDrive(FakeRef) { if (useOs) osDisk else programDisk }
    val networkCard = DeviceNetworkCard(FakeRef)

    networkCard.debugLevel = 5

    val cpu = CPU_MIPS()
    val memory = RAM(0x20000, true)

    val rom = if (args.isNotEmpty()) CustomRom(args[0]) else ROM("assets/$MOD_ID/cpu/bios.bin")

    val bus = Bus(memory, mutableMapOf())
    val motherboard = Motherboard(cpu, memory, rom, bus)
    val mbDevice = DeviceMotherboard(FakeRef, motherboard)

    bus.devices[0xFF] = mbDevice
    bus.devices[0x00] = monitor
    bus.devices[0x01] = floppyDrive
    bus.devices[0x02] = networkCard


    val display = createDisplay(monitor)

    println("Start")

    //start pc
    motherboard.reset()
    cpu.debugLevel = 2
    motherboard.cyclesPerTick = 200000
    motherboard.start()

    timer = System.currentTimeMillis()
    while (motherboard.isOnline()) {
        networkCard.update()
        floppyDrive.iterate()
        //run CPU
        motherboard.iterate()

        //update display
        display.revalidate()
        display.repaint()

        //update world time
        timer = System.currentTimeMillis()
        val tick = (timer.and(0xFFFFFF) / 50L).toInt()
        if (tick != lastTick) {
            lastTick = tick
        }
        Thread.sleep(50)
    }
    println("End")

    // This is used to avoid: 'Disconnected from the target VM' in the middle of the CPU output
    System.out.flush()
    Thread.sleep(10)
}

private fun createDisplay(monitor: DeviceMonitor): MonitorWindow {
    val display = MonitorWindow(monitor)
    val window = JFrame("Emulator")

    val box = Box(BoxLayout.Y_AXIS)
    box.alignmentX = JComponent.CENTER_ALIGNMENT
    box.add(Box.createVerticalGlue())
    box.add(display)
    box.add(Box.createVerticalGlue())

    window.contentPane.apply { background = Color.DARK_GRAY.darker().darker() }
    window.add(box)
    window.pack()

    window.setSize(8 * 80 + 10 + 16, 16 * 35 + 3 + 39)
    window.isVisible = true
    window.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE

    window.addKeyListener(object : KeyListener {
        override fun keyTyped(e: KeyEvent) {
            if (e.keyChar.toInt() == 10 && useOs) {
                useOs = false
                println("Disk changed!")
            }
            val ibd = IBD()
            monitor.onKeyPressed(mapKey(e.keyChar.toInt()))
            monitor.saveToServer(ibd)
            monitor.loadFromClient(ibd)
        }

        override fun keyPressed(e: KeyEvent) {
            val code = when(e.keyCode){
                37 -> 203
                39 -> 205
                else -> 0
            }
            if (code != 0) {
                val ibd = IBD()
                monitor.onKeyPressed(code)
                monitor.saveToServer(ibd)
                monitor.loadFromClient(ibd)
            }
        }

        override fun keyReleased(e: KeyEvent?) = Unit
    })
    return display
}

fun mapKey(code: Int): Int = when (code) {
    10 -> 13
    else -> code
}

class MonitorWindow(val monitor: DeviceMonitor) : JPanel() {
    init {
        val y = 16 * 35 + 3
        val x = 8 * 80 + 10
        maximumSize = Dimension(x, y)
        minimumSize = Dimension(x, y)
        preferredSize = Dimension(x, y)
        background = Color.BLACK
        font = Font("monospaced", Font.PLAIN, 12)
    }

    override fun paint(g: Graphics?) {
        super.paint(g)
        val graphics = g as? Graphics2D ?: return
        val lines = monitor.lines
        val columns = monitor.columns
        var selected = false

        graphics.color = Color.GREEN
        for (line in 0 until lines) {
            for (column in 0 until columns) {
                val character = monitor.getChar(line * columns + column) and 0xFF
                if (line == monitor.cursorLine && column == monitor.cursorColumn && lastTick % 20 >= 10) {
                    selected = true
                }
                if (character != 0x20 || selected) {
                    val x = 8 * column + 4
                    val y = 16 * line + 15
                    if (selected) {
                        graphics.color = Color.GREEN
                        graphics.fillRect(x, y - 13, 10, 14)
                        graphics.color = Color.BLACK
                        graphics.drawString(String.format("%c", character.toChar()), x, y)
                        graphics.color = Color.GREEN
                    } else {
                        graphics.drawString(String.format("%c", character.toChar()), x, y)
                    }
                }
                selected = false
            }
        }
    }
}

class CustomRom(val str: String) : IROM {
    override fun getBIOS(): InputStream = File(str).inputStream()
}

class FakeFloppyDisk(val file: File, val readOnly: Boolean) : IFloppyDisk {

    override fun getStorageFile(): File {
        return file
    }

    override fun getSerialNumber(): Int = 0x0000_ABCD

    override fun getLabel(): String = "fake_floppy_disk"

    override fun setLabel(str: String) = Unit

    override fun getSectorCount(): Int = 128

    override fun getAccessTime(): Int = 1

    override fun canRead(): Boolean = true

    override fun canWrite(): Boolean = !readOnly
}

//     bios string print
//    print("\n\n\n\n\n")
//    println(rom.bios.reader(Charsets.US_ASCII).readText())
//    print("\n\n\n\n\n")
//
////     bios instruction decompiler
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
//    print("\n\n\n\n\n")
//
////     bios hexdump
//    rom.bios.use {
//        var index = 0
//        var lineCounter = 0
//        while (true) {
//            val r = it.read()
//            if (r == -1) break
//            if (lineCounter == 0) {
//                lineCounter = 16
//                print("\n 0x" + "%04x ".format(index))
//            }
//            print("%02x ".format(r))
//            lineCounter--
//
//            memory.writeByte(index++, r.toByte())
//        }
//        println("\nBios size: $index bytes")
//    }
//    print("\n\n\n\n\n")
