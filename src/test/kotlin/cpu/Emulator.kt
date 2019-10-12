package cpu

import com.cout970.magneticraft.MOD_ID
import com.cout970.magneticraft.api.computer.IFloppyDisk
import com.cout970.magneticraft.api.computer.IROM
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.computer.*
import org.lwjgl.input.Keyboard
import java.awt.*
import java.awt.event.InputEvent.CTRL_MASK
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.io.File
import java.io.InputStream
import javax.swing.*

/**
 * Created by cout970 on 2017/06/23.
 */


fun main(args: Array<String>) {
    Emulator.init(args)
}

object Emulator {
    private var useOs = true
    private lateinit var motherboard: Motherboard

    fun init(args: Array<String>) {
        val img = "rust_lisp"
        val osDisk = FakeFloppyDisk(File("./src/main/resources/assets/magneticraft/cpu/$img.bin"), true)
        val programDisk = FakeFloppyDisk(File("./run/disk.img"), false)

        val monitor = DeviceMonitor()
        val keyboard = DeviceKeyboard()
        val floppyDrive = DeviceFloppyDrive { if (useOs) osDisk else programDisk }
        val networkCard = DeviceNetworkCard(FakeRef)

        networkCard.debugLevel = 5

        val cpu = CPU_MIPS()
        val memory = RAM(0x20000, true)

        val rom = if (args.isNotEmpty()) CustomRom(args[0]) else ROM("assets/$MOD_ID/cpu/bios.bin")

        motherboard = Motherboard(cpu, memory, rom)
        val mbDevice = DeviceMotherboard(FakeRef, motherboard)

        motherboard.deviceMap.put(0xFF, mbDevice)
        motherboard.deviceMap.put(0x00, monitor)
        motherboard.deviceMap.put(0x01, floppyDrive)
        motherboard.deviceMap.put(0x02, keyboard)
        motherboard.deviceMap.put(0x03, networkCard)

        val display = createDisplay(monitor, keyboard)

        //start pc
        restart()

        while (true) {
            // "Game loop"
            while (motherboard.isOnline) {
                networkCard.update()
                floppyDrive.update()
                //run CPU
                motherboard.iterate()

                //update display
                display.revalidate()
                display.repaint()

                Thread.sleep(50)
            }
            println("End")

            while (!motherboard.isOnline) {
                Thread.sleep(10)
            }
        }

        // This is used to avoid: 'Disconnected from the target VM' in the middle of the CPU output
//        System.out.flush()
//        Thread.sleep(10)
    }

    private fun restart() {
        useOs = true

        motherboard.reset()
        (motherboard.cpu as CPU_MIPS).debugLevel = 2
        motherboard.cyclesPerTick = 200000
        motherboard.start()

        println("Start")
    }

    private fun createDisplay(monitor: DeviceMonitor, keyboard: DeviceKeyboard): MonitorWindow {
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
        window.focusTraversalKeysEnabled = false

        window.addKeyListener(object : KeyListener {
            override fun keyTyped(e: KeyEvent) {
                if (e.keyChar.toInt() == 10 && useOs) {
                    useOs = false
                    println("Disk changed!")
                }

                if (e.modifiers and CTRL_MASK > 0 && e.keyCode != 17) {
                    e.keyChar += 96
                }

                if (e.keyChar.toInt() in 32..126) {
                    val ibd = IBD()
                    keyboard.onKeyPress(e.keyChar.toInt(), 0)
                    keyboard.saveToServer(ibd)
                    keyboard.loadFromClient(ibd)
                }
            }

            override fun keyPressed(e: KeyEvent) {
                if (e.extendedKeyCode == 116) {
                    restart()
                } else if (e.extendedKeyCode == 117) {
                    motherboard.reset()
                }

                if (e.keyChar.toInt() != 0xFFFF && e.modifiers and CTRL_MASK > 0) {
                    return
                }
                if (e.keyChar.toInt() !in 32..126) {
                    val ibd = IBD()
                    keyboard.onKeyPress(mapKey(e.keyCode), mapKeyCode(e.keyCode))
                    keyboard.saveToServer(ibd)
                    keyboard.loadFromClient(ibd)
                }
            }

            override fun keyReleased(e: KeyEvent) {
                val ibd = IBD()
                keyboard.onKeyRelease(mapKey(e.keyCode), mapKeyCode(e.keyCode))
                keyboard.saveToServer(ibd)
                keyboard.loadFromClient(ibd)
            }
        })
        return display
    }

    fun mapKey(code: Int): Int = when (code) {
        20 -> 0 // caps lock

        38 -> 1 // UP
        40 -> 2 // DOWN
        37 -> 3 // LEFT
        39 -> 4 // RIGHT

        36 -> 5 // HOME
        33 -> 6 // PRIOR (re-pag)
        35 -> 7 // END
        34 -> 8 // NEXT (av-pag)
        155 -> 10 // INSERT
        127 -> 13 // DELETE

        8 -> 11 // BACK
        9 -> 9 // TAB
        10 -> 10 // RETURN
        17 -> 14 // LCONTROL

        16 -> 15 // LSHIFT
//    16 -> 16 // RSHIFT
        18 -> 17 // LMENU

        112 -> 18 // F1
        113 -> 19 // F2
        114 -> 20 // F3
        115 -> 21 // F4
        116 -> 22 // F5
        117 -> 23 // F6
        118 -> 24 // F7
        119 -> 25 // F8
        120 -> 26 // F9
        121 -> 27 // F10

//    17 -> 28 // RCONTROL
//    18 -> 29 // RMENU

        524 -> 30 // LMETA
        525 -> 31 // RMETA
        else -> code
    }

    fun mapKeyCode(code: Int): Int = when (code) {
        20 -> 0 // caps lock

        38 -> Keyboard.KEY_UP // UP
        40 -> Keyboard.KEY_DOWN // DOWN
        37 -> Keyboard.KEY_LEFT // LEFT
        39 -> Keyboard.KEY_RIGHT // RIGHT

        36 -> Keyboard.KEY_HOME // HOME
        33 -> Keyboard.KEY_PRIOR // PRIOR (re-pag)
        35 -> Keyboard.KEY_END // END
        34 -> Keyboard.KEY_NEXT // NEXT (av-pag)
        155 -> Keyboard.KEY_INSERT // INSERT
        127 -> Keyboard.KEY_DELETE // DELETE

        8 -> Keyboard.KEY_BACK // BACK
        9 -> Keyboard.KEY_TAB // TAB
        10 -> Keyboard.KEY_RETURN // RETURN
        17 -> Keyboard.KEY_LCONTROL // LCONTROL

        16 -> Keyboard.KEY_LSHIFT // LSHIFT
//    16 -> 16 // RSHIFT
        18 -> Keyboard.KEY_LMENU // LMENU

        112 -> Keyboard.KEY_F1 // F1
        113 -> Keyboard.KEY_F2 // F2
        114 -> Keyboard.KEY_F3 // F3
        115 -> Keyboard.KEY_F4 // F4
        116 -> Keyboard.KEY_F5 // F5
        117 -> Keyboard.KEY_F6 // F6
        118 -> Keyboard.KEY_F7 // F7
        119 -> Keyboard.KEY_F8 // F8
        120 -> Keyboard.KEY_F8 // F9
        121 -> Keyboard.KEY_F10 // F10

//    17 -> 28 // RCONTROL
//    18 -> 29 // RMENU

        524 -> Keyboard.KEY_LMETA // LMETA
        525 -> Keyboard.KEY_RMETA // RMETA
        else -> code
    }
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
                if (line == monitor.cursorLine && column == monitor.cursorColumn) {
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
