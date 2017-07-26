package com.cout970.magneticraft.computer

import com.cout970.magneticraft.api.computer.IDevice
import com.cout970.magneticraft.api.core.ITileRef
import com.cout970.magneticraft.api.core.NodeID
import com.cout970.magneticraft.config.Config
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.net.Socket
import java.nio.charset.Charset

/**
 * Created by cout970 on 2016/10/31.
 */
class DeviceNetworkCard(val parent: ITileRef) : IDevice, ITickable, ITileRef by parent {

    val status = 0
    val isActive: Boolean = true
    val internetAllowed: Boolean get() = Config.allowTcpConnections
    val maxSockets get() = Config.maxTcpConnections

    val targetIp = ByteArray(80)
    var targetPort = 0

    var targetMac = 0

    val inputBuffer = ByteArray(1024)
    var inputBufferPtr = 0
    val outputBuffer = ByteArray(1024)
    var outputBufferPtr = 0

    var connectionError = 0

    var socket: Socket? = null

    companion object {
        var activeSockets = 0

        const val NO_ERROR = 0
        const val INVALID_PORT = 1
        const val INVALID_IP_SIZE = 2
        const val EXCEPTION_PARSING_IP = 3
        const val EXCEPTION_OPEN_SOCKET = 4
        const val INTERNET_NOT_ALLOWED = 5
        const val MAX_SOCKET_REACH = 6
        const val SOCKET_CLOSED = 7
        const val UNABLE_TO_READ_PACKET = 8
        const val UNABLE_TO_SEND_PACKET = 9
        const val INVALID_OUTPUT_BUFFER_POINTER = 10
        const val INVALID_INPUT_BUFFER_POINTER = 11
    }

    override fun getId(): NodeID = NodeID("module_device_network_card", pos, world)

    val memStruct = ReadWriteStruct("network_header",
            ReadWriteStruct("device_header",
                    ReadOnlyByte("online", { if (isActive) 1 else 0 }),
                    ReadOnlyByte("type", { 2 }),
                    ReadOnlyShort("status", { status.toShort() })
            ),
            ReadOnlyByte("internetAllowed", { if (internetAllowed) 1 else 0 }),
            ReadOnlyByte("maxSockets", { maxSockets.toByte() }),
            ReadOnlyByte("activeSockets", { activeSockets.toByte() }),
            ReadWriteByte("signal", { signal(it.toInt()) }, { 0 }),
            ReadOnlyInt("macAddress", { getMacAddress() }),
            ReadWriteInt("targetMac", { targetMac = it }, { targetMac }),
            ReadWriteInt("targetPort", { targetPort = it }, { targetPort }),
            ReadWriteByteArray("targetIp", targetIp),
            ReadOnlyInt("connectionOpen", { if (socket?.isClosed ?: true) 0 else 1 }),
            ReadOnlyInt("connectionError", { connectionError }),
            ReadWriteInt("inputBufferPtr", { inputBufferPtr = it }, { inputBufferPtr }),
            ReadWriteInt("outputBufferPtr", { outputBufferPtr = it }, { outputBufferPtr }),
            ReadWriteByteArray("inputBuffer", inputBuffer),
            ReadWriteByteArray("outputBuffer ", outputBuffer)
    )

    fun getMacAddress(): Int {
        if(parent is FakeRef){
            return 0xABCDEF01.toInt()
        }
        return parent.pos.hashCode()
    }

    override fun update() {
        socket?.let {
            if (it.isClosed) {
                connectionError = SOCKET_CLOSED
                closeTcpConnection()
            } else {
                try {
                    if (outputBufferPtr > outputBuffer.size || outputBufferPtr < 0) {
                        connectionError = INVALID_OUTPUT_BUFFER_POINTER
                    } else if (outputBufferPtr > 0) {
                        it.getOutputStream().write(outputBuffer, 0, outputBufferPtr)
                        outputBufferPtr = 0
                    }
                } catch (e: Exception) {
                    connectionError = UNABLE_TO_SEND_PACKET
                    e.printStackTrace()
                }
                try {
                    if (inputBufferPtr > inputBuffer.size || inputBufferPtr < 0) {
                        connectionError = INVALID_INPUT_BUFFER_POINTER
                    } else {
                        val stream = it.getInputStream()
                        if (stream.available() > 0) {
                            val read = stream.read(inputBuffer, inputBufferPtr, inputBuffer.size - inputBufferPtr)
                            inputBufferPtr += read
                        }
                    }
                } catch (e: Exception) {
                    connectionError = UNABLE_TO_READ_PACKET
                    e.printStackTrace()
                }
            }
        }
    }

    fun signal(signal: Int) {
        when (signal) {
            1 -> openTcpConnection()
            2 -> closeTcpConnection()
        }
    }

    fun closeTcpConnection(){
        socket?.let {
            activeSockets--
            it.close()
        }
        socket = null
    }

    fun openTcpConnection() {
        if (!internetAllowed) {
            connectionError = INTERNET_NOT_ALLOWED
            return
        }
        if (activeSockets >= maxSockets) {
            connectionError = MAX_SOCKET_REACH
            return
        }
        if (targetPort <= 0 || targetPort > 0xFFFF) {
            connectionError = INVALID_PORT
            return
        }
        val ipStr: String
        try {
            val size = targetIp.indexOf(0)
            if (size == -1 || size >= 80) {
                connectionError = INVALID_IP_SIZE
                return
            }
            val tmp = ByteArray(size)
            System.arraycopy(targetIp, 0, tmp, 0, size)
            ipStr = tmp.toString(Charset.forName("US-ASCII"))
        } catch (e: Exception) {
            e.printStackTrace()
            connectionError = EXCEPTION_PARSING_IP
            return
        }
        try {
            socket = Socket(ipStr, targetPort)
            connectionError = NO_ERROR
            activeSockets++
        } catch (e: Exception) {
            e.printStackTrace()
            connectionError = EXCEPTION_OPEN_SOCKET
            closeTcpConnection()
        }
    }

    override fun readByte(addr: Int): Byte {
        return memStruct.read(addr)
    }

    override fun writeByte(addr: Int, data: Byte) {
        memStruct.write(addr, data)
    }

    override fun getWorld(): World = parent.world

    override fun getPos(): BlockPos = parent.pos

    override fun deserializeNBT(nbt: NBTTagCompound?) = Unit

    override fun serializeNBT(): NBTTagCompound = NBTTagCompound()
}