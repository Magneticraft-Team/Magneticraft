package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.split
import net.minecraft.nbt.NBTTagCompound

/**
 * Created by cout970 on 31/12/2015.
 */
class DeviceMonitor(val parent: ITileRef) : IDevice, ITileRef by parent {

    private var screenBuffer: ByteArray? = null

    //keyboard buffer
    var regKeyBufferPtr = 0
    var regKeyBufferSize = 0
    private var keyBuffer: ByteArray? = null

    //mouse buffer
    var regMouseBufferPtr = 0
    var regMouseBufferSize = 0
    private var mouseBuffer: ByteArray? = null

    //screen buffer
    var cursorLine = 0
    var cursorColumn = 0
    var currentLine = 0
    private var buffer: ByteArray? = null

    //client aux vars
    var isKeyPressed = false
    var keyPressed = 0

    var isMousePressed = false
    var mousePressed = 0
    var mouseX = 0
    var mouseY = 0

    val lines: Int = 35
    val columns: Int = 80
    val screenSize: Int = lines * columns
    val isActive: Boolean = true

    override fun getId(): NodeID = NodeID("module_device_monitor", pos, world)

    //@formatter:off
    val memStruct = ReadWriteStruct("monitor_header",
            ReadWriteStruct("device_header",
                    ReadOnlyByte("online", { if (isActive) 1 else 0 }),
                    ReadOnlyByte("type", { 1 }),
                    ReadOnlyShort("status", { 0 })
            ),
            ReadWriteByte("keyBufferPtr", { regKeyBufferPtr = it.toInt() and 0xFF }, { regKeyBufferPtr.toByte() }),
            ReadWriteByte("keyBufferSize", { regKeyBufferSize = it.toInt() and 0xFF }, { regKeyBufferSize.toByte() }),
            ReadWriteByteArray("keyBuffer", getKeyBuffer()),
            ReadWriteByte("mouseBufferPtr", { regMouseBufferPtr = it.toInt() and 0xFF }, { regMouseBufferPtr.toByte() }),
            ReadWriteByte("mouseBufferSize", { regMouseBufferSize = it.toInt() and 0xFF }, { regMouseBufferSize.toByte() }),
            ReadWriteByteArray("mouseBuffer", getMouseBuffer()),
            ReadOnlyInt("lines", { lines }),
            ReadOnlyInt("columns", { columns }),
            ReadWriteInt("cursorLine", { cursorLine = it }, { cursorLine }),
            ReadWriteInt("cursorColumn", { cursorColumn = it }, { cursorColumn }),
            ReadWriteShort("signal", { signal(it.toInt()) }, { 0 }),
            ReadWriteShort("currentLine", { currentLine = it.toInt() }, { currentLine.toShort() }),
            ReadWriteByteArray("buffer", getBuffer())
    )
    //@formatter:on

    fun signal(id: Int) {
        when (id) {
            1 -> if (currentLine in 0..(lines - 1)) {
                for (i in 0 until columns) {
                    getBuffer()[i] = getScreenBuffer()[currentLine * columns + i]
                }
            }
            2 -> if (currentLine in 0..(lines - 1)) {
                for (i in 0 until columns) {
                    getScreenBuffer()[currentLine * columns + i] = getBuffer()[i]
                }
            }
        }
    }

    override fun readByte(pointer: Int): Byte {
        return memStruct.read(pointer)
    }

    override fun writeByte(pointer: Int, data: Byte) {
        memStruct.write(pointer, data)
    }

    fun getBuffer(): ByteArray {
        if (buffer == null || buffer!!.size != 80) buffer = ByteArray(80)
        return buffer!!
    }

    fun getScreenBuffer(): ByteArray {
        if (screenBuffer == null || screenBuffer!!.size != screenSize) screenBuffer = ByteArray(screenSize)
        return screenBuffer!!
    }

    fun getKeyBuffer(): ByteArray {
        if (keyBuffer == null || keyBuffer!!.size != 28) keyBuffer = ByteArray(28)
        return keyBuffer!!
    }

    fun getMouseBuffer(): ByteArray {
        if (mouseBuffer == null || mouseBuffer!!.size != 36) mouseBuffer = ByteArray(36)
        return mouseBuffer!!
    }

    fun getChar(pos: Int): Int {
        return getScreenBuffer()[pos].toInt()
    }

    fun onKeyPressed(key: Int) {
        isKeyPressed = true
        keyPressed = key
    }

    // Not sure if i want to implement this
    @Suppress("unused")
    fun onCursorClick(x: Int, y: Int, button: Int) {
        isMousePressed = true
        mouseX = x
        mouseY = y
        mousePressed = button
    }

    override fun deserializeNBT(main: NBTTagCompound) {
        val nbt = main.getCompoundTag("OldMonitor")
        regKeyBufferPtr = nbt.getInteger("KeyBufferPtr")
        regKeyBufferSize = nbt.getInteger("KeyBufferSize")
        System.arraycopy(nbt.getByteArray("KeyBuffer"), 0, getKeyBuffer(), 0, getKeyBuffer().size)
        regMouseBufferPtr = nbt.getInteger("MouseBufferPtr")
        regMouseBufferSize = nbt.getInteger("MouseBufferSize")
        System.arraycopy(nbt.getByteArray("MouseBuffer"), 0, getMouseBuffer(), 0, getMouseBuffer().size)
        System.arraycopy(nbt.getByteArray("Buffer"), 0, getBuffer(), 0, getBuffer().size)
        screenBuffer = nbt.getByteArray("ScreenBuffer").clone()
        currentLine = nbt.getInteger("CurrentLine")
        cursorLine = nbt.getInteger("CursorLine")
        cursorColumn = nbt.getInteger("CursorColumn")
        getScreenBuffer()
    }

    override fun serializeNBT(): NBTTagCompound {
        val main = NBTTagCompound()
        val nbt = NBTTagCompound()
        nbt.setInteger("KeyBufferPtr", regKeyBufferPtr)
        nbt.setInteger("KeyBufferSize", regKeyBufferSize)
        nbt.setByteArray("KeyBuffer", getKeyBuffer())
        nbt.setInteger("MouseBufferPtr", regMouseBufferPtr)
        nbt.setInteger("MouseBufferSize", regMouseBufferSize)
        nbt.setByteArray("MouseBuffer", getMouseBuffer())
        nbt.setByteArray("Buffer", getBuffer())
        nbt.setByteArray("ScreenBuffer", getScreenBuffer())
        nbt.setInteger("CurrentLine", currentLine)
        nbt.setInteger("CursorLine", cursorLine)
        nbt.setInteger("CursorColumn", cursorColumn)
        main.setTag("OldMonitor", nbt)
        return main
    }

    //Client to Server sync
    fun saveToServer(ibd: IBD) {
        ibd.setInteger(0, if (isKeyPressed) 1 else 0)
        ibd.setInteger(1, keyPressed)
        isKeyPressed = false

        ibd.setInteger(2, if (isMousePressed) 1 else 0)
        ibd.setInteger(3, mousePressed)
        ibd.setInteger(4, mouseX)
        ibd.setInteger(5, mouseY)
        isMousePressed = false
    }

    fun loadFromClient(ibd: IBD) {

        ibd.getInteger(0) {
            val key = ibd.getInteger(1)

            if (regKeyBufferSize != getKeyBuffer().size / 2) {
                val pos = (regKeyBufferPtr + regKeyBufferSize) % (keyBuffer!!.size / 2)
                getKeyBuffer()[2 * pos] = 1
                getKeyBuffer()[2 * pos + 1] = key.toByte()
                regKeyBufferSize++
            }
        }

        ibd.getInteger(2) {
            val mouseButton = ibd.getInteger(3)
            val mouseX = ibd.getInteger(4)
            val mouseY = ibd.getInteger(5)

            if (regMouseBufferSize != getMouseBuffer().size / 6) {
                val pos = ((regMouseBufferPtr + regMouseBufferSize) * 6) % keyBuffer!!.size
                getMouseBuffer()[pos] = mouseButton.toByte()
                getMouseBuffer()[pos + 1] = 1
                getMouseBuffer()[pos + 2] = mouseX.split(0)
                getMouseBuffer()[pos + 3] = mouseX.split(1)
                getMouseBuffer()[pos + 4] = mouseY.split(0)
                getMouseBuffer()[pos + 5] = mouseY.split(1)
                regMouseBufferSize++
            }
        }
    }

    //Server to Client sync
    fun saveToClient(ibd: IBD) {
        ibd.setInteger(0, cursorLine)
        ibd.setInteger(1, cursorColumn)
        ibd.setByteArray(2, getScreenBuffer())
    }

    fun loadFromServer(ibd: IBD) {
        ibd.getInteger(0) { cursorLine = it }
        ibd.getInteger(1) { cursorColumn = it }
        ibd.getByteArray(2) { System.arraycopy(it, 0, getScreenBuffer(), 0, screenSize) }
    }
}
