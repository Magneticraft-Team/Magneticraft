package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.gui.DATA_ID_MONITOR_BUFFER
import com.cout970.magneticraft.systems.gui.DATA_ID_MONITOR_CURSOR_COLUMN
import com.cout970.magneticraft.systems.gui.DATA_ID_MONITOR_CURSOR_LINE

/**
 * Created by cout970 on 31/12/2015.
 */
class DeviceMonitor : IDevice {

    private var screenBuffer: ByteArray? = null

    //screen buffer
    var cursorLine = 0
    var cursorColumn = 0
    var currentLine = 0
    private var lineBuffer: ByteArray? = null

    val lines: Int = 34
    val columns: Int = 80
    val screenSize: Int = lines * columns

    //@formatter:off
    val memStruct = ReadWriteStruct("monitor_header",
            ReadWriteStruct("device_header",
                    ReadOnlyByte("online", { 1 }),
                    ReadOnlyByte("type", { 1 }),
                    ReadOnlyShort("status", { 0 })
            ),
            ReadOnlyInt("lines", { lines }),
            ReadOnlyInt("columns", { columns }),
            ReadWriteInt("cursorLine", { cursorLine = it }, { cursorLine }),
            ReadWriteInt("cursorColumn", { cursorColumn = it }, { cursorColumn }),
            ReadWriteShort("signal", { signal(it.toInt()) }, { 0 }),
            ReadWriteShort("currentLine", { currentLine = it.toInt() }, { currentLine.toShort() }),
            ReadWriteByteArray("buffer", getBuffer())
    )
    //@formatter:on

    override fun update() = Unit

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

    override fun readByte(bus: IRW, pointer: Int): Byte {
        return memStruct.read(pointer)
    }

    override fun writeByte(bus: IRW, pointer: Int, data: Byte) {
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

    fun getChar(pos: Int): Int {
        return getScreenBuffer()[pos].toInt()
    }

    override fun serialize() = mapOf(
        "Buffer" to getBuffer().copyOf(),
        "ScreenBuffer" to getScreenBuffer().copyOf(),
        "CurrentLine" to currentLine,
        "CursorLine" to cursorLine,
        "CursorColumn" to cursorColumn
    )

    override fun deserialize(map: Map<String, Any>) {
        System.arraycopy(map["Buffer"] as ByteArray, 0, getBuffer(), 0, getBuffer().size)
        screenBuffer = map["ScreenBuffer"] as ByteArray
        currentLine = map["CurrentLine"] as Int
        cursorLine = map["CursorLine"] as Int
        cursorColumn = map["CursorColumn"] as Int
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
