package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.util.split
import com.cout970.magneticraft.util.splitRange
import com.cout970.magneticraft.util.splitSet
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Created by cout970 on 31/12/2015.
 */
class DeviceMonitor(val parent: TileEntity) : IDevice {

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
    val isActive: Boolean get() = !parent.isInvalid

    override fun readByte(pointer: Int): Byte {
        val byte = when (pointer) {
            0 -> (if (isActive) 1 else 0).toByte()               //00, byte online
            1 -> 1.toByte()                                      //01, byte type
            2, 3 -> 0.toByte()                                   //02,03, short status
            4 -> regKeyBufferPtr.toByte()                        //04, byte keyBufferPtr
            5 -> regKeyBufferSize.toByte()                       //05, byte keyBufferSize
            in 6..19 -> getKeyBuffer()[pointer - 6]              //06, byte[14] keyBuffer
            20 -> regMouseBufferPtr.toByte()                     //20, mouseBufferPtr
            21 -> regKeyBufferSize.toByte()                      //21, mouseBufferSize
            in 22..58 -> getMouseBuffer()[pointer - 22]          //22, struct mouse[6]
            in 60.splitRange() -> lines.split(pointer - 60)      //60, int lines
            in 64.splitRange() -> columns.split(pointer - 64)    //64, int columns
            in 68.splitRange() -> cursorLine.split(pointer - 68) //68, short cursorLine
            in 72.splitRange() -> cursorColumn.split(pointer - 72)//72, short cursorColumn
            76, 77 -> 0                                          //76, order
            78, 79 -> currentLine.split(pointer - 78)            //78, currentLine
            in 80..159 -> getBuffer()[pointer - 80]              //80, buffer
            else -> 0
        }
//        println("Read: pointer: $pointer, byte: $byte")
        return byte
    }

    override fun writeByte(pointer: Int, data: Byte) {
//        println("Write: pointer: $pointer, byte: $data")
        when (pointer) {
            4 -> regKeyBufferPtr = data.toInt()
            5 -> regKeyBufferSize = data.toInt()
            in 6..19 -> getKeyBuffer()[pointer - 6] = data
            20 -> regMouseBufferPtr = data.toInt()
            21 -> regMouseBufferSize = data.toInt()
            in 22..57 -> getMouseBuffer()[pointer - 22] = data
            in 68.splitRange() -> cursorLine = cursorLine.splitSet(pointer - 68, data)
            in 72.splitRange() -> cursorColumn = cursorColumn.splitSet(pointer - 72, data)
            76 -> {
                if (data.toInt() == 1) {
                    if (currentLine >= 0 && currentLine < lines) {
                        for (i in 0 until columns) {
                            getBuffer()[i] = getScreenBuffer()[currentLine * columns + i]
                        }
                    }
                } else if (data.toInt() == 2) {
                    if (currentLine >= 0 && currentLine < lines) {
                        for (i in 0 until columns) {
                            getScreenBuffer()[currentLine * columns + i] = getBuffer()[i]
                        }
                    }
                }
            }
            78, 79 -> {
                currentLine = currentLine.splitSet(pointer - 78, data)
            }
            in 80..159 -> getBuffer()[pointer - 80] = data
        }
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
        if (keyBuffer == null || keyBuffer!!.size != 14) keyBuffer = ByteArray(14)
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
        keyBuffer = nbt.getByteArray("KeyBuffer").clone()
        getKeyBuffer()
        regMouseBufferPtr = nbt.getInteger("MouseBufferPtr")
        regMouseBufferSize = nbt.getInteger("MouseBufferSize")
        mouseBuffer = nbt.getByteArray("MouseBuffer").clone()
        getMouseBuffer()
        buffer = nbt.getByteArray("Buffer").clone()
        getBuffer()
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

    override fun getWorld(): World = parent.world

    override fun getPos(): BlockPos = parent.pos

    //Client to Server sync
    fun saveToServer(ibd: IBD) {
        ibd.setInteger(0, if (isKeyPressed) 1 else 0)
        ibd.setInteger(1, keyPressed)

        ibd.setInteger(2, if (isMousePressed) 1 else 0)
        ibd.setInteger(3, mousePressed)
        ibd.setInteger(4, mouseX)
        ibd.setInteger(5, mouseY)
    }

    fun loadFromClient(ibd: IBD) {

        ibd.getInteger(0) {
            val key = ibd.getInteger(1)

            if (regKeyBufferSize != getKeyBuffer().size) {
                getKeyBuffer()[(regKeyBufferPtr + regKeyBufferSize) % keyBuffer!!.size] = key.toByte()
                regKeyBufferSize++
            }
        }

        ibd.getInteger(2) {
            val mouseButton = ibd.getInteger(3)
            val mouseX = ibd.getInteger(4)
            val mouseY = ibd.getInteger(5)

            if (regMouseBufferSize != getMouseBuffer().size) {
                getMouseBuffer()[((regMouseBufferPtr + regMouseBufferSize) * 5) % keyBuffer!!.size] = mouseButton.toByte()
                getMouseBuffer()[((regMouseBufferPtr + regMouseBufferSize) * 5 + 1) % keyBuffer!!.size] = mouseX.split(0)
                getMouseBuffer()[((regMouseBufferPtr + regMouseBufferSize) * 5 + 2) % keyBuffer!!.size] = mouseX.split(1)
                getMouseBuffer()[((regMouseBufferPtr + regMouseBufferSize) * 5 + 3) % keyBuffer!!.size] = mouseY.split(0)
                getMouseBuffer()[((regMouseBufferPtr + regMouseBufferSize) * 5 + 4) % keyBuffer!!.size] = mouseY.split(1)
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
