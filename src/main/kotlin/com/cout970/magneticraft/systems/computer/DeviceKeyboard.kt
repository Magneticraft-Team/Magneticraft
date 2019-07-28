package com.cout970.magneticraft.systems.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.computer.IRW
import com.cout970.magneticraft.api.computer.IResettable
import com.cout970.magneticraft.misc.network.IBD
import com.cout970.magneticraft.systems.gui.DATA_ID_KEYBOARD_EVENT_CODE
import com.cout970.magneticraft.systems.gui.DATA_ID_KEYBOARD_EVENT_KEY
import com.cout970.magneticraft.systems.gui.DATA_ID_KEYBOARD_KEY_STATES
import com.cout970.magneticraft.systems.gui.DATA_ID_MONITOR_CLIPBOARD
import java.util.*

class DeviceKeyboard : IDevice, IResettable {

    //keyboard buffer
    var regKeyBufferPtr = 0
    var regKeyBufferSize = 0

    val keyBuffer = ByteArray(32)
    val keyStates = ByteArray(256)

    var currentKey = 0

    data class KeyEvent(val key: Int, val code: Int)

    val events = ArrayDeque<KeyEvent>()
    val clipboardToPaste = StringBuilder()

    val memStruct = ReadWriteStruct("monitor_header",
        ReadWriteStruct("device_header",
            ReadOnlyByte("online", { 1 }),
            ReadOnlyByte("type", { 1 }),
            ReadOnlyShort("status", { 0 })
        ),
        ReadWriteByte("keyBufferPtr", { regKeyBufferPtr = it.toInt() and 0xFF }, { regKeyBufferPtr.toByte() }),
        ReadWriteByte("keyBufferSize", { regKeyBufferSize = it.toInt() and 0xFF }, { regKeyBufferSize.toByte() }),
        ReadWriteByte("key", { currentKey = it.toInt() and 0xFF }, { currentKey.toByte() }),
        ReadOnlyByte("isPressed", { keyStates[currentKey] }),
        ReadWriteByteArray("keyBuffer", keyBuffer)
    )

    override fun update() {
        if (clipboardToPaste.isNotEmpty()) {

            var key = clipboardToPaste[0]

            while (regKeyBufferSize != keyBuffer.size / 2) {

                val pos = (regKeyBufferPtr + regKeyBufferSize) % (keyBuffer.size / 2)
                keyBuffer[2 * pos] = 1
                keyBuffer[2 * pos + 1] = key.toByte()

                regKeyBufferSize++
                clipboardToPaste.deleteCharAt(0)

                if (clipboardToPaste.isEmpty()) break
                key = clipboardToPaste[0]
            }
        }
    }

    override fun reset() {
        clipboardToPaste.delete(0, clipboardToPaste.length)
        keyStates.fill(0)
    }

    fun onKeyPress(key: Int, code: Int) {
        keyStates[code and 0xFF] = 1
        events.addLast(KeyEvent(key, code))
    }

    @Suppress("UNUSED_PARAMETER")
    fun onKeyRelease(key: Int, code: Int) {
        keyStates[code and 0xFF] = 0
    }

    //Client to Server sync
    fun saveToServer(ibd: IBD) {
        if (events.isNotEmpty()) {
            val e = events.removeFirst()
            ibd.setInteger(DATA_ID_KEYBOARD_EVENT_KEY, e.key)
            ibd.setInteger(DATA_ID_KEYBOARD_EVENT_CODE, e.code)
        }
        ibd.setByteArray(DATA_ID_KEYBOARD_KEY_STATES, keyStates)
    }

    fun loadFromClient(ibd: IBD) {

        ibd.getString(DATA_ID_MONITOR_CLIPBOARD) {
            clipboardToPaste.append(it)
        }

        ibd.getInteger(DATA_ID_KEYBOARD_EVENT_KEY) { key ->
            val code = ibd.getInteger(DATA_ID_KEYBOARD_EVENT_CODE)

            if (regKeyBufferSize != keyBuffer.size / 2) {
                val pos = (regKeyBufferPtr + regKeyBufferSize) % (keyBuffer.size / 2)
                keyBuffer[2 * pos] = key.toByte()
                keyBuffer[2 * pos + 1] = code.toByte()
                regKeyBufferSize++
            }
        }

        ibd.getByteArray(DATA_ID_KEYBOARD_KEY_STATES) { buffer ->
            System.arraycopy(buffer, 0, keyStates, 0, buffer.size)
        }
    }

    override fun writeByte(bus: IRW, addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    override fun readByte(bus: IRW, addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun serialize(): MutableMap<String, Any> = mutableMapOf(
        "KeyBufferPtr" to regKeyBufferPtr,
        "KeyBufferSize" to regKeyBufferSize,
        "currentKey" to currentKey,
        "KeyBuffer" to keyBuffer.copyOf()
    )

    override fun deserialize(map: MutableMap<String, Any>) {
        regKeyBufferPtr = map["KeyBufferPtr"] as Int
        regKeyBufferSize = map["KeyBufferSize"] as Int
        currentKey = map["currentKey"] as Int
        System.arraycopy(map["KeyBuffer"] as ByteArray, 0, keyBuffer, 0, keyBuffer.size)
    }
}