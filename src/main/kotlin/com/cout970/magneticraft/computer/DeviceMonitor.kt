package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IResettable
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.gui.common.core.*
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.split
import java.lang.StringBuilder

/**
 * Created by cout970 on 31/12/2015.
 */
class DeviceMonitor(val parent: ITileRef) : IDevice, IResettable {

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
    private var lineBuffer: ByteArray? = null

    //client aux vars
    var isKeyPressed = false
    var keyPressed = 0

    var isMousePressed = false
    var mousePressed = 0
    var mouseX = 0
    var mouseY = 0

    val lines: Int = 34
    val columns: Int = 80
    val screenSize: Int = lines * columns

    val clipboardToPaste = StringBuilder()

    //@formatter:off
    val memStruct = ReadWriteStruct("monitor_header",
            ReadWriteStruct("device_header",
                    ReadOnlyByte("online", { 1 }),
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

    override fun reset(){
        clipboardToPaste.delete(0, clipboardToPaste.length)
    }

    fun update() {
        if (clipboardToPaste.isNotEmpty()) {

            var key = clipboardToPaste[0]
            val buff = getKeyBuffer()

            while (regKeyBufferSize != buff.size / 2) {

                val pos = (regKeyBufferPtr + regKeyBufferSize) % (keyBuffer!!.size / 2)
                buff[2 * pos] = 1
                buff[2 * pos + 1] = key.toByte()

                regKeyBufferSize++
                clipboardToPaste.deleteCharAt(0)

                if (clipboardToPaste.isEmpty()) break
                key = clipboardToPaste[0]
            }
        }
    }

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
        if (lineBuffer == null || lineBuffer!!.size != columns) lineBuffer = ByteArray(columns)
        return lineBuffer!!
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

    override fun serialize() = mapOf(
            "KeyBufferPtr" to regKeyBufferPtr,
            "KeyBufferSize" to regKeyBufferSize,
            "KeyBuffer" to getKeyBuffer().copyOf(),
            "MouseBufferPtr" to regMouseBufferPtr,
            "MouseBufferSize" to regMouseBufferSize,
            "MouseBuffer" to getMouseBuffer().copyOf(),
            "Buffer" to getBuffer().copyOf(),
            "ScreenBuffer" to getScreenBuffer().copyOf(),
            "CurrentLine" to currentLine,
            "CursorLine" to cursorLine,
            "CursorColumn" to cursorColumn
    )

    override fun deserialize(map: Map<String, Any>) {
        regKeyBufferPtr = map["KeyBufferPtr"] as Int
        regKeyBufferSize = map["KeyBufferSize"] as Int
        System.arraycopy(map["KeyBuffer"] as ByteArray, 0, getKeyBuffer(), 0, getKeyBuffer().size)
        regMouseBufferPtr = map["MouseBufferPtr"] as Int
        regMouseBufferSize = map["MouseBufferSize"] as Int
        System.arraycopy(map["MouseBuffer"] as ByteArray, 0, getMouseBuffer(), 0, getMouseBuffer().size)
        System.arraycopy(map["Buffer"] as ByteArray, 0, getBuffer(), 0, getBuffer().size)
        screenBuffer = map["ScreenBuffer"] as ByteArray
        currentLine = map["CurrentLine"] as Int
        cursorLine = map["CursorLine"] as Int
        cursorColumn = map["CursorColumn"] as Int
    }

    //Client to Server sync
    fun saveToServer(ibd: IBD) {
        ibd.setInteger(DATA_ID_MONITOR_HAS_KEY, if (isKeyPressed) 1 else 0)
        ibd.setInteger(DATA_ID_MONITOR_KEY, keyPressed)
        isKeyPressed = false

        ibd.setInteger(DATA_ID_MONITOR_MOUSE_CLICK, if (isMousePressed) 1 else 0)
        ibd.setInteger(DATA_ID_MONITOR_MOUSE_BUTTON, mousePressed)
        ibd.setInteger(DATA_ID_MONITOR_MOUSE_X, mouseX)
        ibd.setInteger(DATA_ID_MONITOR_MOUSE_Y, mouseY)
        isMousePressed = false
    }

    fun loadFromClient(ibd: IBD) {

        ibd.getString(DATA_ID_MONITOR_CLIPBOARD) {
            clipboardToPaste.append(it)
        }

        ibd.getInteger(DATA_ID_MONITOR_HAS_KEY) {
            val key = ibd.getInteger(DATA_ID_MONITOR_KEY)

            if (regKeyBufferSize != getKeyBuffer().size / 2) {
                val pos = (regKeyBufferPtr + regKeyBufferSize) % (keyBuffer!!.size / 2)
                getKeyBuffer()[2 * pos] = 1
                getKeyBuffer()[2 * pos + 1] = key.toByte()
                regKeyBufferSize++
            }
        }

        ibd.getInteger(DATA_ID_MONITOR_MOUSE_CLICK) {
            val mouseButton = ibd.getInteger(DATA_ID_MONITOR_MOUSE_BUTTON)
            val mouseX = ibd.getInteger(DATA_ID_MONITOR_MOUSE_X)
            val mouseY = ibd.getInteger(DATA_ID_MONITOR_MOUSE_Y)

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
        ibd.setInteger(DATA_ID_MONITOR_CURSOR_LINE, cursorLine)
        ibd.setInteger(DATA_ID_MONITOR_CURSOR_COLUMN, cursorColumn)
        ibd.setByteArray(DATA_ID_MONITOR_BUFFER, getScreenBuffer())
    }

    fun loadFromServer(ibd: IBD) {
        ibd.getInteger(DATA_ID_MONITOR_CURSOR_LINE) { cursorLine = it }
        ibd.getInteger(DATA_ID_MONITOR_CURSOR_COLUMN) { cursorColumn = it }
        ibd.getByteArray(DATA_ID_MONITOR_BUFFER) { System.arraycopy(it, 0, getScreenBuffer(), 0, screenSize) }
    }
}
